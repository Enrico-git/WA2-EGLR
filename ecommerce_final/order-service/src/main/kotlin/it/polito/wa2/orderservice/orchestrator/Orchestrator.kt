package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.domain.Product
import it.polito.wa2.orderservice.domain.ProductLocation
import it.polito.wa2.orderservice.domain.toRedisStateMachine
import it.polito.wa2.orderservice.domain.toStateMachine
import it.polito.wa2.orderservice.dto.*
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import it.polito.wa2.orderservice.services.MailServiceImpl
import it.polito.wa2.orderservice.statemachine.StateMachine
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

@Component
class Orchestrator(
    @Qualifier("new_order_sm") private val stateMachineBuilder: StateMachineBuilder,
    @Qualifier("delete_order_sm") private val deleteOrderstateMachineBuilder: StateMachineBuilder,
    private val kafkaPaymentReqProducer: KafkaProducer<String, PaymentRequestDTO>,
    private val kafkaProdResReqProducer: KafkaProducer<String, ProductsReservationRequestDTO>,
    private val kafkaAbortProdResReqProducer: KafkaProducer<String, AbortProductReservationRequestDTO>,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val orderRepository: OrderRepository,
    private val redisStateMachineRepository: RedisStateMachineRepository,
    private val jobs: ConcurrentHashMap<String, Job>,
    private val logger: Logger,
    private val mailService: MailServiceImpl,
    private val orchestratorActions: OrchestratorActions
) {
    @Value("\${application.kafka.retryDelay}")
    val delay: Long = 0

    @Lookup
    @Lazy
    fun getListOfStateMachine(): ConcurrentHashMap<String, StateMachine> {
        return null!!
    }

    suspend fun createSaga(sagaDTO: SagaDTO) {
        val sagas = getListOfStateMachine()
        val stateMachine = if (sagaDTO.type == "new_order")
            stateMachineBuilder
                .id(sagaDTO.id)
                .amount(sagaDTO.amount)
                .products(sagaDTO.products!!)
                .auth(sagaDTO.auth)
                .customerEmail(sagaDTO.customerEmail)
                .build()
        else
            deleteOrderstateMachineBuilder
                .id(sagaDTO.id)
                .amount(sagaDTO.amount)
                .productsWarehouseLocation(sagaDTO.productsWarehouseLocation!!)
                .auth(sagaDTO.auth)
                .customerEmail(sagaDTO.customerEmail)
                .build()
        stateMachine.start()
        sagas[sagaDTO.id] = stateMachine
        if (sagaDTO.type == "new_order")
            stateMachine.send(StateMachineEvents.RESERVE_PRODUCTS)
        else
            stateMachine.send(StateMachineEvents.ABORT_PAYMENT_REQUEST)
        logger.info("STATE MACHINE ${stateMachine.id} STARTED")
    }

    @KafkaListener(topics = [
        "reserve_products_ok",
        "reserve_products_failed",
        "payment_request_failed",
        "payment_request_ok",
        "abort_products_reservation_ok",
        "abort_products_reservation_failed",
        "abort_payment_request_ok",
        "abort_payment_request_failed"
    ])
    fun receivedEvent(event: String) = CoroutineScope(Dispatchers.IO).launch {
        val sagas = getListOfStateMachine()
        val sagaEvent = StateMachineEvents.valueOf(event.substringAfter("-").uppercase())
        val sagaID = event.substringBefore("-")
        val saga = sagas[sagaID]
        println(saga)
        val transition = saga?.transitions?.find{it.source == saga.state && it.event == sagaEvent}
        println("${saga?.id} --- ${saga?.state} --- $sagaEvent ---- trans: $transition")
        saga?.send(transition?.event!!)
    }

    @EventListener
    fun onStateMachineEvent(event: StateMachineEvent) {
        val sm = event.source as StateMachine
        when (StateMachineEvents.valueOf(event.event.substringAfter("-"))) {
            StateMachineEvents.RESERVE_PRODUCTS -> orchestratorActions.onReserveProducts(sm)
            StateMachineEvents.RESERVE_PRODUCTS_OK -> orchestratorActions.onReserveProductsOk(sm)
            StateMachineEvents.RESERVE_PRODUCTS_FAILED -> orchestratorActions.onReserveProductsFailed(sm)
            StateMachineEvents.PAYMENT_REQUEST -> orchestratorActions.onPaymentRequest(sm)
            StateMachineEvents.PAYMENT_REQUEST_OK -> orchestratorActions.onPaymentRequestOk(sm)
            StateMachineEvents.PAYMENT_REQUEST_FAILED -> orchestratorActions.onPaymentRequestFailed(sm)
            StateMachineEvents.ABORT_PAYMENT_REQUEST -> orchestratorActions.onAbortPaymentRequest(sm)
            StateMachineEvents.ABORT_PAYMENT_REQUEST_OK -> orchestratorActions.onAbortPaymentRequestOk(sm)
            StateMachineEvents.ABORT_PAYMENT_REQUEST_FAILED -> orchestratorActions.onAbortPaymentRequestFailed(sm)
            StateMachineEvents.ABORT_PRODUCTS_RESERVATION -> orchestratorActions.onAbortProductsReservation(sm)
            StateMachineEvents.ABORT_PRODUCTS_RESERVATION_OK -> orchestratorActions.onAbortProductsReservationOk(sm)
            StateMachineEvents.ABORT_PRODUCTS_RESERVATION_FAILED -> orchestratorActions.onAbortProductsReservationFailed(sm)
        }
    }

    @EventListener
    fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo) = CoroutineScope(
        Dispatchers.Default).launch {
        val sm = event.source as StateMachine
        jobs["${sm.id}-${event.event}"]?.cancel()
    }

    @EventListener
    fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent){
        val sm = sagaFinishedEvent.source as StateMachine
        CoroutineScope(Dispatchers.IO).launch {
            val order = orderRepository.findById(ObjectId(sm.id))
            if (sm.state!! == StateMachineStates.ORDER_ISSUED)
                order!!.status = OrderStatus.ISSUED
            else if (sm.state!! == StateMachineStates.ORDER_CANCELED)
                order!!.status = OrderStatus.CANCELED
            orderRepository.save(order!!)
            mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  sm.finalState.toString().substringAfter("_"))
            mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  sm.finalState.toString().substringAfter("_"))
        }
        logger.info("SAGA OF ORDER ${sm.id} ENDED SUCCESSFULLY")
    }

    @EventListener
    fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent){
        val sm = sagaFailureEvent.source as StateMachine
        CoroutineScope(Dispatchers.IO).launch {
            val order = orderRepository.findById(ObjectId(sm.id))
            if (sm.finalState == StateMachineStates.ORDER_ISSUED){
                order!!.status = OrderStatus.FAILED
                orderRepository.save(order)
                mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  "ISSUE_FAILED")
                mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  "ISSUE_FAILED")
            }
            else if (sm.finalState == StateMachineStates.ORDER_CANCELED){
                mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  "CANCELLATION_FAILED")
                mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  "CANCELLATION_FAILED")
            }
        }
        logger.info("SAGA OF ORDER ${sm.id} FAILED ")
    }

    /**
     * On startup check if there are pending sagas by querying redis
     * if there are, this service crashed before completing them
     * @param event the event emitted when all beans are loaded
     */
    @EventListener
    fun onStartUp(event: ContextRefreshedEvent) = CoroutineScope(Dispatchers.Default).launch{
        val sagas = getListOfStateMachine()
        redisStateMachineRepository.getAll().onEach {
            println("RESUMING: $it")
            sagas[it.id] = it.toStateMachine(
                if (it.initialState == StateMachineStates.ORDER_REQ)
                    stateMachineBuilder
                else
                    deleteOrderstateMachineBuilder
            )
            sagas[it.id]!!.resume()
        }.collect()
    }
}
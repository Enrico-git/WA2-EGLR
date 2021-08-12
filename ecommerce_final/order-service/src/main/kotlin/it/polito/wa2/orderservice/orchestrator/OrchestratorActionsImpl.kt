package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.common.EmailType
import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.domain.toStateMachine
import it.polito.wa2.orderservice.dto.*
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import it.polito.wa2.orderservice.services.MailServiceImpl
import it.polito.wa2.orderservice.services.OrderService
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
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
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.mongodb.UncategorizedMongoDbException
import org.springframework.stereotype.Component
import org.springframework.util.ConcurrentReferenceHashMap
import java.lang.IllegalArgumentException
import java.sql.Timestamp
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

@Component
class OrchestratorActionsImpl(
    @Qualifier("new_order_sm") private val stateMachineBuilder: StateMachineBuilder,
    @Qualifier("delete_order_sm") private val deleteOrderStateMachineBuilder: StateMachineBuilder,
    private val redisStateMachineRepository: RedisStateMachineRepository,
    private val kafkaPaymentReqProducer: KafkaProducer<String, PaymentRequestDTO>,
    private val kafkaProdResReqProducer: KafkaProducer<String, ProductsReservationRequestDTO>,
    private val kafkaAbortProdResReqProducer: KafkaProducer<String, AbortProductReservationRequestDTO>,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val orderRepository: OrderRepository,
    @Lazy private val orderService: OrderService,
    private val jobs: ConcurrentReferenceHashMap<String, Job>,
    private val logger: Logger,
    private val mailService: MailServiceImpl
) : OrchestratorActions{
    @Value("\${application.kafka.retryDelay}")
    val delay: Long = 0

    @Value("\${application.kafka.numberOfRetries}")
    val numberOfRetries: Int = 0

    @Lookup
    @Lazy
    override fun getListOfStateMachine(): ConcurrentHashMap<String, StateMachineImpl> {
        return null!!
    }

    override fun onApplicationStartUp(event: ContextRefreshedEvent) = CoroutineScope(Dispatchers.Default).launch{
        val sagas = getListOfStateMachine()
        redisStateMachineRepository.getAll().onEach {
            println("RESUMING: $it")
            sagas[it.id] = it.toStateMachine(
                if (it.initialState == StateMachineStates.ORDER_REQ)
                    stateMachineBuilder
                else
                    deleteOrderStateMachineBuilder
            )
            sagas[it.id]?.resume()
        }.collect()
    }

    override suspend fun createSaga(sagaDTO: SagaDTO) {
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
            deleteOrderStateMachineBuilder
                .id(sagaDTO.id)
                .amount(sagaDTO.amount)
                .productsWarehouseLocation(sagaDTO.productsWarehouseLocation!!)
                .auth(sagaDTO.auth)
                .customerEmail(sagaDTO.customerEmail)
                .build()
        sagas[sagaDTO.id] = stateMachine
        stateMachine.start()
        logger.info("STATE MACHINE ${stateMachine.id} STARTED")
    }


    override fun onKafkaReceivedStringEvent(event: String, topic: String) = CoroutineScope(Dispatchers.Default).launch {
        val sagas = getListOfStateMachine()
        val sagaEvent = StateMachineEvents.valueOf(topic.uppercase())
        val saga = sagas[event]
        println(saga)
        saga?.nextStateAndFireEvent(sagaEvent)
    }

    override fun onKafkaReceivedProductsReservationOKEvent(event: ProductsReservationResponseDTO): Job = CoroutineScope(Dispatchers.Default).launch {
        val sagas = getListOfStateMachine()
        val sagaEvent = StateMachineEvents.RESERVE_PRODUCTS_OK
        val saga = sagas[event.orderID]
        println(saga)
        saga?.nextStateAndFireEvent(sagaEvent, event.productsWarehouseLocation)
    }

    override fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo) = CoroutineScope(
        Dispatchers.Default).launch {
        val sm = event.source as StateMachineImpl
        jobs["${sm.id}-${event.event}"]?.cancel()
    }

    override fun onReserveProducts(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.RESERVE_PRODUCTS}"] = CoroutineScope(Dispatchers.IO).launch Job@{
            repeat(numberOfRetries) {
                if (isActive)
                    try {
                        kafkaProdResReqProducer.send(
                            ProducerRecord(
                                "reserve_products", ProductsReservationRequestDTO(
                                    orderID = sm.id,
                                    products = sm.products!!,
                                    timestamp = Timestamp(System.currentTimeMillis())
                                )
                            )
                        )
                        delay(delay)
                    } catch (e: CancellationException) {
                        return@Job
                    }
            }
            sm.nextStateAndFireEvent(StateMachineEvents.RESERVE_PRODUCTS_FAILED)
        }
    }

    override fun onReserveProductsOk(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.RESERVE_PRODUCTS
            )
        )
        CoroutineScope(Dispatchers.IO).launch Job@{
            val order = orderRepository.findById(ObjectId(sm.id))!!
            order.delivery.productsWarehouseLocation = sm.productsWarehouseLocation
            var counter = 5
            while (counter-- > 0)
                try {
                    orderRepository.save(order)
                    return@Job
                } catch (e: UncategorizedMongoDbException) {
                    delay(1000)
                } catch (e: OptimisticLockingFailureException){
                    delay(1000)
                } catch (e: Exception) {
                    logger.severe("Could not add products location of order ${sm.id}")
                }
            logger.severe("Could not add products location of order ${sm.id} due to Optimistic Locking Failure")
        }
        sm.nextStateAndFireEvent(StateMachineEvents.PAYMENT_REQUEST)
    }

    override fun onReserveProductsFailed(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.RESERVE_PRODUCTS
            )
        )
    }

    override fun onPaymentRequest(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.PAYMENT_REQUEST}"] = CoroutineScope(Dispatchers.IO).launch Job@{
            repeat(numberOfRetries) {
                if (isActive)
                    try {
                        kafkaPaymentReqProducer.send(
                            ProducerRecord(
                                "payment_request",
                                PaymentRequestDTO(sm.id, sm.amount, sm.auth, Timestamp(System.currentTimeMillis()))
                            )
                        )
                        delay(delay)
                    } catch (e: CancellationException) {
                        return@Job
                    }
            }
            sm.nextStateAndFireEvent(StateMachineEvents.PAYMENT_REQUEST_FAILED)
        }
    }

    override fun onPaymentRequestOk(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.PAYMENT_REQUEST
            )
        )
    }

    override fun onPaymentRequestFailed(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, StateMachineEvents.PAYMENT_REQUEST))
        sm.nextStateAndFireEvent(StateMachineEvents.ABORT_PRODUCTS_RESERVATION)
    }

    override fun onAbortPaymentRequest(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.ABORT_PAYMENT_REQUEST}"] =
            CoroutineScope(Dispatchers.IO).launch Job@{
                repeat(numberOfRetries) {
                    if (isActive)
                        try {
                            kafkaPaymentReqProducer.send(ProducerRecord("abort_payment_request", PaymentRequestDTO(
                                sm.id,
                                sm.amount,
                                sm.auth,
                                Timestamp(System.currentTimeMillis())
                            )
                            ))
                            delay(delay)
                        } catch (e: CancellationException) {
                            return@Job
                        }
                }
                sm.nextStateAndFireEvent(StateMachineEvents.ABORT_PAYMENT_REQUEST_FAILED)
                logger.severe("ABORT PAYMENT FAILED FOR ORDER ${sm.id}")
            }
    }

    override fun onAbortPaymentRequestOk(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.ABORT_PAYMENT_REQUEST
            )
        )
        CoroutineScope(Dispatchers.IO).launch Job@{
            val order = orderRepository.findById(ObjectId(sm.id))!!
            order.delivery.productsWarehouseLocation = setOf()
            var counter = 5
            while (counter-- > 0)
                try {
                    orderRepository.save(order)
                    return@Job
                } catch (e: UncategorizedMongoDbException) {
                    delay(1000)
                } catch (e: OptimisticLockingFailureException){
                    delay(1000)
                } catch (e: Exception) {
                    logger.severe("Could not remove products location of order ${sm.id}")
                }
            logger.severe("Could not remove products location of order ${sm.id} due to Optimistic Locking Failure")
            mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  EmailType.UPDATE_PRODUCTS_ERROR)
        }
        sm.nextStateAndFireEvent(StateMachineEvents.ABORT_PRODUCTS_RESERVATION)
    }

    override fun onAbortPaymentRequestFailed(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.ABORT_PAYMENT_REQUEST
            )
        )
    }

    override fun onAbortProductsReservation(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.ABORT_PRODUCTS_RESERVATION}"] =
            CoroutineScope(Dispatchers.IO).launch Job@{
                repeat(numberOfRetries) {
                    if (isActive)
                        try {
                            kafkaAbortProdResReqProducer.send(
                                ProducerRecord(
                                    "abort_products_reservation",
                                    AbortProductReservationRequestDTO(
                                        sm.id,
                                        sm.productsWarehouseLocation!!,
                                        Timestamp(System.currentTimeMillis())
                                    )
                                ))
                            delay(delay)
                        } catch (e: CancellationException) {
                            return@Job
                        }
                }
                sm.nextStateAndFireEvent(StateMachineEvents.ABORT_PRODUCTS_RESERVATION_FAILED)
            }
    }

    override fun onAbortProductsReservationOk(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, StateMachineEvents.ABORT_PRODUCTS_RESERVATION))
    }

    override fun onAbortProductsReservationFailed(sm: StateMachineImpl) = CoroutineScope(Dispatchers.Default).launch {
        logger.severe("COULD NOT ABORT PRODUCTS RESERVATION FOR ORDER ${sm.id}")
        CoroutineScope(Dispatchers.IO).launch {
            mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id, EmailType.ABORT_PRODUCT_ERROR)
        }
        applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, StateMachineEvents.ABORT_PRODUCTS_RESERVATION))
    }

    override fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent){
        val sm = sagaFinishedEvent.source as StateMachineImpl
        CoroutineScope(Dispatchers.IO).launch {
            lateinit var status: OrderStatus
            if (sm.state!! == StateMachineStates.ORDER_ISSUED)
                status = OrderStatus.ISSUED
            else if (sm.state!! == StateMachineStates.ORDER_CANCELED)
                status = OrderStatus.CANCELED
            var counter = 5
            while (counter-- > 0)
                try {
                    orderService.updateOrderOnSagaEnding(sm, status, EmailType.valueOf(sm.finalState.toString().substringAfter("_")))
                    logger.info("SAGA OF ORDER ${sm.id} ENDED SUCCESSFULLY")
                    return@launch
                } catch (e: UncategorizedMongoDbException) {
                    delay(1000)
                } catch( e: OptimisticLockingFailureException) {
                    delay(1000)
                } catch (e: IllegalArgumentException) {
                    logger.severe("Could not find order ${sm.id}")
                    return@launch
                }
            logger.severe("Could not update order ${sm.id} status to $status")
            mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  EmailType.UPDATE_STATUS_ERROR, status)
        }
    }

    override fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent){
        val sm = sagaFailureEvent.source as StateMachineImpl
        CoroutineScope(Dispatchers.IO).launch {
            if (sm.finalState == StateMachineStates.ORDER_ISSUED) {
                var counter = 5
                while (counter-- > 0)
                    try {
                        orderService.updateOrderOnSagaEnding(sm, OrderStatus.FAILED, EmailType.ISSUE_FAILED)
                        return@launch
                    } catch (e: UncategorizedMongoDbException) {
                        delay(1000)
                    } catch (e: OptimisticLockingFailureException) {
                        delay(1000)
                    } catch (e: IllegalArgumentException) {
                        logger.severe("Could not find order ${sm.id}")
                        return@launch
                    }
                logger.severe("Could not update order ${sm.id} status to ${OrderStatus.FAILED}")
            } else if (sm.finalState == StateMachineStates.ORDER_CANCELED){
                mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  EmailType.CANCELLATION_FAILED)
                mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  EmailType.CANCELLATION_FAILED)
            }
        }
        logger.info("SAGA OF ORDER ${sm.id} FAILED ")
    }

}
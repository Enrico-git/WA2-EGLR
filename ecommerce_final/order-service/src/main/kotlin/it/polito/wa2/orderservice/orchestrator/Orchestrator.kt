package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.domain.PaymentRequest
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import it.polito.wa2.orderservice.repositories.orders.OrderRepository
import it.polito.wa2.orderservice.services.MailServiceImpl
import it.polito.wa2.orderservice.statemachine.StateMachine
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import kotlinx.coroutines.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.logging.Logger

@Component
class Orchestrator(
    @Qualifier("new_order_sm") private val stateMachineBuilder: StateMachineBuilder,
    @Qualifier("delete_order_sm") private val deleteOrderstateMachineBuilder: StateMachineBuilder,
    private val kafkaProducer: KafkaProducer<String, String>,
    private val kafkaPaymentReqProducer: KafkaProducer<String, PaymentRequest>,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val orderRepository: OrderRepository,
    private val jobs: ConcurrentHashMap<String, Job>,
    private val logger: Logger,
    private val mailService: MailServiceImpl
) {


    @Lookup
    @Lazy
    fun getListOfStateMachine(): ConcurrentHashMap<String, StateMachine> {
        return null!!
    }

    //    TODO THIS IS FOR DEBEZIUM
//    @KafkaListener(topics = ["orders_ok"])
//    fun debezium_test(msg: String){
//        println("New Message: $msg")
//    }


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
        val sagaEvent = event.substringAfter("-")
        val sagaID = event.substringBefore("-")
        val saga = sagas[sagaID]
        val transition = saga?.transitions?.find{it.source == saga.state && it.event == sagaEvent}
        println("${saga?.id} --- ${saga?.state} --- $sagaEvent ---- trans: $transition")
        saga?.send(transition?.event!!)
    }

    suspend fun createSaga(id: String, type: String, customerEmail: String, amount: BigDecimal? = null, auth: String) {
        val sagas = getListOfStateMachine()
        val stateMachine = if (type == "new_order")
            stateMachineBuilder.id(id).amount(amount!!).auth(auth).customerEmail(customerEmail).build()
        else
            deleteOrderstateMachineBuilder.id(id).auth(auth).customerEmail(customerEmail).build()
        stateMachine.start()
        sagas[id] = stateMachine
        if (type == "new_order")
            stateMachine.send("reserve_products")
        else
            stateMachine.send("abort_payment_request")
        logger.info("STATE MACHINE ${stateMachine.id} STARTED")
    }

    @EventListener
    fun onStateMachineEvent(event: StateMachineEvent) {
        val sm = event.source as StateMachine
        when (event.event.substringAfter("-")) {
            "reserve_products" -> jobs["${sm.id}-reserve_products"] = CoroutineScope(Dispatchers.IO).launch {
                repeat(5) {
                    if (isActive)
                        try {
                            kafkaProducer.send(ProducerRecord("reserve_products", event.event))
                            delay(5000)

                        } catch (e: CancellationException) {
                            return@launch
                        }
                }
                sm.send("reserve_products_failed")
            }
            "reserve_products_ok" -> CoroutineScope(Dispatchers.Default).launch {
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "reserve_products"))
                sm.send("payment_request")
            }
            "reserve_products_failed" ->{
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "reserve_products"))
            }
            "payment_request" -> jobs["${sm.id}-payment_request"] = CoroutineScope(Dispatchers.IO).launch {
                repeat(5) {
                    if (isActive)
                        try {
//                            kafkaProducer.send(ProducerRecord("payment_request", event.event))
                            kafkaPaymentReqProducer.send(ProducerRecord("payment_request", PaymentRequest(sm.id, sm.amount!!, sm.auth  )))
                            delay(5000)
                        } catch (e: CancellationException) {
                            return@launch
                        }
                }
                sm.send("payment_request_failed")
            }
            "payment_request_ok" -> {
                applicationEventPublisher.publishEvent(
                    KafkaResponseReceivedEventInResponseTo(
                        sm,
                        "payment_request"
                    )
                )
            }
            "payment_request_failed" -> CoroutineScope(Dispatchers.Default).launch {
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "payment_request"))
                sm.send("abort_products_reservation")
            }
            "abort_payment_request" -> jobs["${sm.id}-abort_payment_request"] =
                    CoroutineScope(Dispatchers.IO).launch {
                        repeat(5) {
                            if (isActive)
                                try {
                                    kafkaProducer.send(ProducerRecord("abort_payment_request", event.event))
                                    delay(5000)
                                } catch (e: CancellationException) {
                                    return@launch
                                }
                        }
                        sm.send("abort_payment_request_failed")
                        logger.severe("ABORT PAYMENT FAILED FOR ORDER ${sm.id}")
                    }
            "abort_payment_request_ok" -> CoroutineScope(Dispatchers.Default).launch {
                applicationEventPublisher.publishEvent(
                    KafkaResponseReceivedEventInResponseTo(
                        sm,
                        "abort_payment_request"
                    )
                )
                sm.send("abort_products_reservation")
            }
            "abort_payment_request_failed" -> {
                applicationEventPublisher.publishEvent(
                    KafkaResponseReceivedEventInResponseTo(
                        sm,
                        "abort_payment_request"
                    )
                )

            }
            "abort_products_reservation" -> jobs["${sm.id}-abort_products_reservation"] =
                    CoroutineScope(Dispatchers.IO).launch {
                        repeat(5) {
                            if (isActive)
                                try {
                                    kafkaProducer.send(ProducerRecord("abort_products_reservation", event.event))
                                    delay(5000)
                                } catch (e: CancellationException) {
                                    return@launch
                                }
                        }
                        sm.send("abort_products_reservation_failed")
                    }
            "abort_products_reservation_ok" -> {
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "abort_products_reservation"))
            }
            "abort_products_reservation_failed" -> {
                logger.severe("COULD NOT ABORT PRODUCTS RESERVATION FOR ORDER ${sm.id}")
                CoroutineScope(Dispatchers.IO).launch {
                    mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id, "ERROR")
                }
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "abort_products_reservation"))
            }
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
        sm.completed = true
        CoroutineScope(Dispatchers.IO).launch {
            val order = orderRepository.findById(ObjectId(sm.id))
            if (sm.state!! == "ORDER_ISSUED")
                order!!.status = OrderStatus.ISSUED
            else if (sm.state!! == "ORDER_CANCELED")
                order!!.status = OrderStatus.CANCELED
            orderRepository.save(order!!)
            mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  sm.finalState.substringAfter("_"))
            mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  sm.finalState.substringAfter("_"))
        }
        logger.info("SAGA OF ORDER ${sm.id} ENDED SUCCESSFULLY")
    }

    @EventListener
    fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent){
        val sm = sagaFailureEvent.source as StateMachine
        sm.failed = true
        CoroutineScope(Dispatchers.IO).launch {
            val order = orderRepository.findById(ObjectId(sm.id))
            if (sm.finalState == "ORDER_ISSUED"){
                order!!.status = OrderStatus.FAILED
                orderRepository.save(order)
                mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  "ISSUE_FAILED")
                mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  "ISSUE_FAILED")
            }
            else if (sm.finalState == "ORDER_CANCELED"){
                mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  "CANCELLATION_FAILED")
                mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  "CANCELLATION_FAILED")
            }
        }
        logger.info("SAGA OF ORDER ${sm.id} FAILED ")
    }

}
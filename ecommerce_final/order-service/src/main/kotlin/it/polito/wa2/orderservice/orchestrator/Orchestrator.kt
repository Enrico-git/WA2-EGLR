package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.domain.OrderJob
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import it.polito.wa2.orderservice.repositories.orders.OrderRepository
import it.polito.wa2.orderservice.statemachine.StateMachine
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import kotlinx.coroutines.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CopyOnWriteArrayList
import java.util.logging.Logger

@Component
class Orchestrator(
    @Qualifier("new_order_sm") private val stateMachineBuilder: StateMachineBuilder,
    @Qualifier("delete_order_sm") private val deleteOrderstateMachineBuilder: StateMachineBuilder,
    private val kafkaProducer: KafkaProducer<String, String>,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val orderRepository: OrderRepository,
    private val jobs: CopyOnWriteArrayList<OrderJob>,
    private val logger: Logger
) {


    @Lookup
    @Lazy
    fun getListOfStateMachine(): CopyOnWriteArrayList<StateMachine> {
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
        val saga = sagas.find{it.id == sagaID}
        val transition = saga?.transitions?.find{it.source == saga.state && it.event == sagaEvent}
        println("${saga?.id} --- ${saga?.state} --- $sagaEvent ---- trans: $transition")
        saga?.send(transition?.event!!)
    }

    suspend fun createSaga(id: String, type: String) {
        val sagas = getListOfStateMachine()
        val stateMachine = if (type == "new_order")
            stateMachineBuilder.id(id).build()
        else
            deleteOrderstateMachineBuilder.id(id).build()
        stateMachine.start()
        sagas.add(stateMachine)
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
            "reserve_products" -> jobs.add(OrderJob("${sm.id}-reserve_products", CoroutineScope(Dispatchers.IO).launch {
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
            }))
            "reserve_products_ok" -> CoroutineScope(Dispatchers.Default).launch {
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "reserve_products"))
                sm.send("payment_request")
            }
            "reserve_products_failed" ->{
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "reserve_products"))
            }
            "payment_request" -> jobs.add(OrderJob("${sm.id}-payment_request", CoroutineScope(Dispatchers.IO).launch {
                repeat(5) {
                    if (isActive)
                        try {
                            kafkaProducer.send(ProducerRecord("payment_request", event.event))
                            delay(5000)
                        } catch (e: CancellationException) {
                            return@launch
                        }
                }
                sm.send("payment_request_failed")
            }))
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
            "abort_payment_request" -> jobs.add(
                OrderJob(
                    "${sm.id}-abort_payment_request",
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
                    })
            )
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
            "abort_products_reservation" -> jobs.add(
                OrderJob(
                    "${sm.id}-abort_products_reservation",
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
                    })
            )
            "abort_products_reservation_ok" -> {
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "abort_products_reservation"))
            }
            "abort_products_reservation_failed" -> {
                logger.severe("COULD NOT ABORT PRODUCTS RESERVATION FOR ORDER ${sm.id}")
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "abort_products_reservation"))
            }
        }
    }

    @EventListener
    fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo) = CoroutineScope(
        Dispatchers.Default).launch {
        val sm = event.source as StateMachine
//        println("${sm.id}-${event.event}")
//        var job: Job? = null
//        lock.withLock {
//            val it = jobs.iterator()
//            while (it.hasNext()){
//                val orderJob = it.next()
//                if (orderJob.first == "${sm.id}-${event.event}") {
//                    job = orderJob.second

                    val job = jobs.find { it.first == "${sm.id}-${event.event}" }?.second
                    job?.cancel()
//                    break
//                }
            }
//        }
//        jobs.removeIf { it.second.key == job?.key }
//    }

    @EventListener
    fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent){
        val sm = sagaFinishedEvent.source as StateMachine
        sm.completed = true
//        sagas.removeIf { it.id == sm.id }
        //        TODO CHANGE ORDER STATUS FROM PENDING TO READY USING COROUTINE
        //        controller -> new order [pending]
        //        controller -> preso in carico con id 1
        //        controller -> parte la saga con id 1
        //        endsaga -> fine saga -> [verificato] -> invia la mail
        CoroutineScope(Dispatchers.IO).launch {
            val order = orderRepository.findById(ObjectId(sm.id))
            order!!.status = OrderStatus.ISSUED
            orderRepository.save(order)
//            TODO SEND MAIL
        }
        logger.info("SAGA OF ORDER ${sm.id} ENDED SUCCESSFULLY")
    }

    @EventListener
    fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent){
        val sm = sagaFailureEvent.source as StateMachine
        sm.failed = true
//            applicationEventPublisher.send(KafkaResponseReceivedEventInResponseTo(sm, "reserve_products_failed"))
//        sagas.removeIf { it.id == sm.id }
        //        TODO CHANGE ORDER STATUS FROM PENDING TO failure USING COROUTINE
        //        controller -> new order [pending]
        //        controller -> preso in carico con id 1
        //        controller -> parte la saga con id 1
        //        endsaga -> fine saga -> [verificato] -> invia la mail
        CoroutineScope(Dispatchers.IO).launch {
            val order = orderRepository.findById(ObjectId(sm.id))
            order!!.status = OrderStatus.FAILED
            orderRepository.save(order)
//            TODO SEND MAIL
        }
        logger.info("SAGA OF ORDER ${sm.id} FAILED ")
    }

}
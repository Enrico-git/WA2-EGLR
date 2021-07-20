package it.polito.wa2.orderservice.orchestrator

import com.netflix.discovery.EurekaClient
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import it.polito.wa2.orderservice.statemachine.StateMachine
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import kotlinx.coroutines.*
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration

@Component
class Orchestrator(
    @Qualifier("new_order_sm") private val stateMachineBuilder: StateMachineBuilder,
    private val kafkaProducer: KafkaProducer<String, String>,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    val sagas = mutableListOf<StateMachine>()
    val jobs = mutableListOf<Pair<String, Job>>()

    //    TODO THIS IS FOR DEBEZIUM
    @KafkaListener(topics = ["orders_ok"])
    fun debezium_test(msg: String){
        println("New Message: $msg")
    }


    @KafkaListener(topics = ["reserve_products_ok", "reserve_products_failed", "payment_request_failed", "abort_products_reservation_ok"])
    fun receivedEvent(event: String) = CoroutineScope(Dispatchers.IO).launch {
        val sagaEvent = event.substringAfter("-")
        val sagaID = event.substringBefore("-")
        val saga = sagas.find{it.id == sagaID}
        val transition = saga?.transitions?.find{it.source == saga.state && it.event == sagaEvent}
        saga?.send(transition?.event!!)
    }

    //        @EventListener(ApplicationReadyEvent::class)
    //        fun spawnSagas()= CoroutineScope(Dispatchers.Default).launch {
    //            for (i in 0..2)
    //                createSaga()
    //        }


    suspend fun createSaga(id: String) {
        val stateMachine = stateMachineBuilder.id(id).build()
        stateMachine.start()
        sagas.add(stateMachine)
        //        println("aggiunto $stateMachine a sagas")
        stateMachine.send("reserve_products")
    }

    @EventListener
    fun onStateMachineEvent(event: StateMachineEvent) {
        val sm = event.source as StateMachine
        when (event.event.substringAfter("-")) {
            "reserve_products" -> jobs.add(Pair("${sm.id}-reserve_products", CoroutineScope(Dispatchers.IO).launch {
                repeat(5) {
                    if (isActive)
                        try {
                            println("sending kafka msg")
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
            "reserve_products_failed" ->
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "reserve_products"))
            "payment_request" -> jobs.add(Pair("${sm.id}-payment_request", CoroutineScope(Dispatchers.IO).launch {
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
            "payment_request_ok" -> applicationEventPublisher.publishEvent(
                KafkaResponseReceivedEventInResponseTo(
                    sm,
                    "payment_request"
                )
            )
            "payment_request_failed" -> CoroutineScope(Dispatchers.Default).launch {
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "payment_request"))
                sm.send("abort_products_reservation")
            }
            "abort_payment_request" -> jobs.add(
                Pair(
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
                    })
            )
            "abort_payment_request_ok" -> applicationEventPublisher.publishEvent(
                KafkaResponseReceivedEventInResponseTo(
                    sm,
                    "abort_payment_request"
                )
            )
            "abort_payment_request_failed" -> applicationEventPublisher.publishEvent(
                KafkaResponseReceivedEventInResponseTo(
                    sm,
                    "abort_payment_request"
                )
            )
            "abort_products_reservation" -> jobs.add(
                Pair(
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
            "abort_products_reservation_ok" -> applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "abort_products_reservation"))
            "abort_products_reservation_failed" -> {
//                TODO LOG AND KILL SAGA
                applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, "abort_products_reservation"))
            }
        }
    }

    @EventListener
    fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo) = CoroutineScope(
        Dispatchers.Default).launch {
        println("onKafkaResponseReceivedEventInResponseTo LISTENER ${Thread.currentThread()}")
        println("RECEIVED EVENT ${event.source}")
        println("CURRENT JOBS LIST $jobs")
        val sm = event.source as StateMachine
        println("${sm.id}-${event.event}")
        val job = jobs.find { it.first == "${sm.id}-${event.event}"}?.second
        job?.cancel()
//      TODO ADD LOCK
        jobs.removeIf { it.second.key == job?.key }
        println("jobs after removal $jobs")
    }

    @EventListener
    fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent){
        println("onSagaFinishedEvent LISTENER ${Thread.currentThread()}")
        val sm = sagaFinishedEvent.source as StateMachine
        sagas.removeIf { it.id == sm.id }
        //        TODO CHANGE ORDER STATUS FROM PENDING TO READY USING COROUTINE
        //        controller -> new order [pending]
        //        controller -> preso in carico con id 1
        //        controller -> parte la saga con id 1
        //        endsaga -> fine saga -> [verificato] -> invia la mail
        println("SAGA OF ORDER ${sm.id} ENDED SUCCESSFULLY")
        println("SAGAS LIST NOW IS: $sagas")
    }

    @EventListener
    fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent){
        println("onSagaFailureEvent LISTENER ${Thread.currentThread()}")
        val sm = sagaFailureEvent.source as StateMachine
//            applicationEventPublisher.send(KafkaResponseReceivedEventInResponseTo(sm, "reserve_products_failed"))
        sagas.removeIf { it.id == sm.id }
        //        TODO CHANGE ORDER STATUS FROM PENDING TO failure USING COROUTINE
        //        controller -> new order [pending]
        //        controller -> preso in carico con id 1
        //        controller -> parte la saga con id 1
        //        endsaga -> fine saga -> [verificato] -> invia la mail
        println("SAGA OF ORDER ${sm.id} FAILED ")
        println("SAGAS LIST NOW IS: $sagas")
    }

}
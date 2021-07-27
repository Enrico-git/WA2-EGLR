package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.domain.ProductLocation
import it.polito.wa2.orderservice.domain.toRedisStateMachine
import it.polito.wa2.orderservice.dto.AbortProductReservationRequestDTO
import it.polito.wa2.orderservice.dto.PaymentRequestDTO
import it.polito.wa2.orderservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.orderservice.services.MailServiceImpl
import it.polito.wa2.orderservice.statemachine.StateMachine
import kotlinx.coroutines.*
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

@Component
class OrchestratorActions(
    private val kafkaPaymentReqProducer: KafkaProducer<String, PaymentRequestDTO>,
    private val kafkaProdResReqProducer: KafkaProducer<String, ProductsReservationRequestDTO>,
    private val kafkaAbortProdResReqProducer: KafkaProducer<String, AbortProductReservationRequestDTO>,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val orderRepository: OrderRepository,
    private val jobs: ConcurrentHashMap<String, Job>,
    private val logger: Logger,
    private val mailService: MailServiceImpl
) {
    @Value("\${application.kafka.retryDelay}")
    val delay: Long = 0

    @Lookup
    @Lazy
    fun getListOfStateMachine(): ConcurrentHashMap<String, StateMachine> {
        return null!!
    }

    fun onReserveProducts(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.RESERVE_PRODUCTS}"] = CoroutineScope(Dispatchers.IO).launch Job@{
            repeat(5) {
                if (isActive)
                    try {
                        kafkaProdResReqProducer.send(
                            ProducerRecord(
                                "reserve_products", ProductsReservationRequestDTO(
                                    orderID = sm.id,
                                    products = sm.products!!
                                )
                            )
                        )
                        delay(delay)
                    } catch (e: CancellationException) {
                        return@Job
                    }
            }
            sm.send(StateMachineEvents.RESERVE_PRODUCTS_FAILED)
        }
    }
    fun onReserveProductsOk(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.RESERVE_PRODUCTS
            )
        )
        CoroutineScope(Dispatchers.IO).launch {
            val order = orderRepository.findById(ObjectId(sm.id))!!
//                    TODO hardcoded warehouse response NEED FIX
            order.delivery!!.productsWarehouseLocation = setOf(ProductLocation("boh", "wh1", 2))

            try {
                orderRepository.save(order)
            } catch (e: Exception) {
                logger.severe("Could not add products location of order ${sm.id}")
            }
        }
        sm.send(StateMachineEvents.PAYMENT_REQUEST)
    }
    fun onReserveProductsFailed(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.RESERVE_PRODUCTS
            )
        )
    }
    fun onPaymentRequest(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.PAYMENT_REQUEST}"] = CoroutineScope(Dispatchers.IO).launch Job@{
            repeat(5) {
                if (isActive)
                    try {
                        kafkaPaymentReqProducer.send(
                            ProducerRecord(
                                "payment_request",
                                PaymentRequestDTO(sm.id, sm.amount, sm.auth)
                            )
                        )
                        delay(delay)
                    } catch (e: CancellationException) {
                        return@Job
                    }
            }
            sm.send(StateMachineEvents.PAYMENT_REQUEST_FAILED)
        }
    }
    fun onPaymentRequestOk(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.PAYMENT_REQUEST
            )
        )
    }
    fun onPaymentRequestFailed(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, StateMachineEvents.PAYMENT_REQUEST))
        sm.send(StateMachineEvents.ABORT_PRODUCTS_RESERVATION)
    }
    fun onAbortPaymentRequest(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.ABORT_PAYMENT_REQUEST}"] =
            CoroutineScope(Dispatchers.IO).launch Job@{
                repeat(5) {
                    if (isActive)
                        try {
                            kafkaPaymentReqProducer.send(ProducerRecord("abort_payment_request", PaymentRequestDTO(
                                sm.id,
                                sm.amount,
                                sm.auth
                            )
                            ))
                            delay(delay)
                        } catch (e: CancellationException) {
                            return@Job
                        }
                }
                sm.send(StateMachineEvents.ABORT_PAYMENT_REQUEST_FAILED)
                logger.severe("ABORT PAYMENT FAILED FOR ORDER ${sm.id}")
            }
    }
    fun onAbortPaymentRequestOk(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.ABORT_PAYMENT_REQUEST
            )
        )
        sm.send(StateMachineEvents.ABORT_PRODUCTS_RESERVATION)
    }
    fun onAbortPaymentRequestFailed(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(
            KafkaResponseReceivedEventInResponseTo(
                sm,
                StateMachineEvents.ABORT_PAYMENT_REQUEST
            )
        )
    }
    fun onAbortProductsReservation(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        jobs["${sm.id}-${StateMachineEvents.ABORT_PRODUCTS_RESERVATION}"] =
            CoroutineScope(Dispatchers.IO).launch Job@{
                repeat(5) {
                    if (isActive)
                        try {
                            kafkaAbortProdResReqProducer.send(
                                ProducerRecord(
                                    "abort_products_reservation",
                                    AbortProductReservationRequestDTO(
                                        sm.id,
                                        sm.productsWarehouseLocation!!
                                    )
                                ))
                            delay(delay)
                        } catch (e: CancellationException) {
                            return@Job
                        }
                }
                sm.send(StateMachineEvents.ABORT_PRODUCTS_RESERVATION_FAILED)
            }
    }
    fun onAbortProductsReservationOk(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, StateMachineEvents.ABORT_PRODUCTS_RESERVATION))
    }
    fun onAbortProductsReservationFailed(sm: StateMachine) = CoroutineScope(Dispatchers.Default).launch {
        logger.severe("COULD NOT ABORT PRODUCTS RESERVATION FOR ORDER ${sm.id}")
        CoroutineScope(Dispatchers.IO).launch {
            mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id, "ERROR")
        }
        applicationEventPublisher.publishEvent(KafkaResponseReceivedEventInResponseTo(sm, StateMachineEvents.ABORT_PRODUCTS_RESERVATION))
    }


}
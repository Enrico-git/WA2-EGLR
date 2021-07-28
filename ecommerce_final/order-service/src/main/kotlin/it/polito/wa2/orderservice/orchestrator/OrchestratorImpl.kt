package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.dto.*
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrchestratorImpl(
    private val orchestratorActions: OrchestratorActions
) : Orchestrator {

    override suspend fun createSaga(sagaDTO: SagaDTO) = orchestratorActions.createSaga(sagaDTO)

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
    override fun onKafkaEvent(event: String) = orchestratorActions.onKafkaReceivedEvent(event)

    @EventListener
    override fun onStateMachineEvent(event: StateMachineEvent) {
        val sm = event.source as StateMachineImpl
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
    override fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo) = orchestratorActions.onKafkaResponseReceivedEventInResponseTo(event)

    @EventListener
    override fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent) = orchestratorActions.onSagaFinishedEvent(sagaFinishedEvent)

    @EventListener
    override fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent) = orchestratorActions.onSagaFailureEvent(sagaFailureEvent)

    /**
     * On startup check if there are pending sagas by querying redis
     * if there are, this service crashed before completing them
     * @param event the event emitted when all beans are loaded
     */
    @EventListener
    override fun onApplicationStartUp(event: ContextRefreshedEvent) = orchestratorActions.onApplicationStartUp(event)
}
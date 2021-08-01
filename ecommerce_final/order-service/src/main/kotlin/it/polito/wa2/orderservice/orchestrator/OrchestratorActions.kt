package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.dto.SagaDTO
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import kotlinx.coroutines.Job
import org.springframework.context.event.ContextRefreshedEvent
import java.util.concurrent.ConcurrentHashMap

interface OrchestratorActions {
    fun getListOfStateMachine(): ConcurrentHashMap<String, StateMachineImpl>
    fun onApplicationStartUp(event: ContextRefreshedEvent): Job
    suspend fun createSaga(sagaDTO: SagaDTO)
    fun onKafkaReceivedEvent(event: String, topic: String): Job
    fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo): Job
    fun onReserveProducts(sm: StateMachineImpl): Job
    fun onReserveProductsOk(sm: StateMachineImpl): Job
    fun onReserveProductsFailed(sm: StateMachineImpl): Job
    fun onPaymentRequest(sm: StateMachineImpl): Job
    fun onPaymentRequestOk(sm: StateMachineImpl): Job
    fun onPaymentRequestFailed(sm: StateMachineImpl): Job
    fun onAbortPaymentRequest(sm: StateMachineImpl): Job
    fun onAbortPaymentRequestOk(sm: StateMachineImpl): Job
    fun onAbortPaymentRequestFailed(sm: StateMachineImpl): Job
    fun onAbortProductsReservation(sm: StateMachineImpl): Job
    fun onAbortProductsReservationOk(sm: StateMachineImpl): Job
    fun onAbortProductsReservationFailed(sm: StateMachineImpl): Job
    fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent)
    fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent)

}
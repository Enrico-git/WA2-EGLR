package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.dto.SagaDTO
import it.polito.wa2.orderservice.events.KafkaResponseReceivedEventInResponseTo
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import kotlinx.coroutines.Job
import org.springframework.context.event.ContextRefreshedEvent

interface Orchestrator {

    /**
     * Create a State Machine object from a sagaDTO, add it to the hash list and start it
     * @param sagaDTO the details of the saga
     */
    suspend fun createSaga(sagaDTO: SagaDTO)

    /**
     * Kafka event listener
     * @param event
     * @param topic
     * @return a coroutine job
     */
    fun onKafkaStringEvent(event: String, topic: String): Job


    /**
     * State machine application event listener
     * @param event
     */
    fun onStateMachineEvent(event: StateMachineEvent)

    /**
     * Application event listener triggered when a kafka event is received to cancel the job
     * @param event
     * @return coroutine job
     */
    fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo): Job

    /**
     * State machine application event listener when saga ends successfully
     * @param event
     */
    fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent)

    /**
     * State machine application event listener when saga fails
     * @param event
     */
    fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent)

    /**
     * Application event listener to check if there are pending state machines
     * @param event
     * @return coroutine job
     */
    fun onApplicationStartUp(event: ContextRefreshedEvent): Job
}
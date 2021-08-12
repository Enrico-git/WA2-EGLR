package it.polito.wa2.orderservice.orchestrator

import it.polito.wa2.orderservice.dto.ProductsReservationResponseDTO
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

    /**
     * Retrieves the suspended state machines from redis
     * @param event the startup event
     * @return a coroutine job
     */
    fun onApplicationStartUp(event: ContextRefreshedEvent): Job

    /**
     * Spawns a new saga
     * @param sagaDTO
     */
    suspend fun createSaga(sagaDTO: SagaDTO)

    /**
     * Perform the next step of the state machine when a string
     * message is received from kafka
     * @param event always equal to the sagaID
     * @param topic the kafka topic
     * @return a coroutine job
     */
    fun onKafkaReceivedStringEvent(event: String, topic: String): Job

    /**
     * Perform the next step of the state machine when a
     * ProductReservationResponseDTO message is received from kafka
     * @param event
     * @return a coroutine job
     */
    fun onKafkaReceivedProductsReservationOKEvent(event: ProductsReservationResponseDTO): Job

    /**
     * Cancels the running jobs that keep sending messages to the microservices because a response was received
     * @param event
     * @return a coroutine job
     */
    fun onKafkaResponseReceivedEventInResponseTo(event: KafkaResponseReceivedEventInResponseTo): Job

    /**
     * Sends the message to the warehouse service to reserve the products
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onReserveProducts(sm: StateMachineImpl): Job

    /**
     * Cancels the product reservation request job and updates the order in the db
     * with the products location
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onReserveProductsOk(sm: StateMachineImpl): Job

    /**
     * Cancels the product reservation request job
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onReserveProductsFailed(sm: StateMachineImpl): Job

    /**
     * Sends the message to the wallet service to perform a transaction
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onPaymentRequest(sm: StateMachineImpl): Job

    /**
     * Cancels the payment request job
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onPaymentRequestOk(sm: StateMachineImpl): Job

    /**
     * Cancels the payment request job, sends signal to start the rollback
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onPaymentRequestFailed(sm: StateMachineImpl): Job

    /**
     * Sends the message to the wallet service to refund the customer
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onAbortPaymentRequest(sm: StateMachineImpl): Job

    /**
     * Cancels the abort payment request job, sends signal to start put the products of the order back into the warehouse
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onAbortPaymentRequestOk(sm: StateMachineImpl): Job

    /**
     * Cancels the abort payment request job
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onAbortPaymentRequestFailed(sm: StateMachineImpl): Job

    /**
     * Sends the message to the warehouse service to put the products of the order back into the warehouse
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onAbortProductsReservation(sm: StateMachineImpl): Job

    /**
     * Cancels the abort product reservation request job
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onAbortProductsReservationOk(sm: StateMachineImpl): Job

    /**
     * Cancels the abort payment request job, notifies the admin about the error
     * @param sm the state machine
     * @return a coroutine job
     */
    fun onAbortProductsReservationFailed(sm: StateMachineImpl): Job

    /**
     * Updates the order information inside the db and notifies customer and admin
     * @param sagaFinishedEvent the event
     */
    fun onSagaFinishedEvent(sagaFinishedEvent: SagaFinishedEvent)

    /**
     * Updates the order information inside the db and notifies customer and admin
     * @param sagaFailureEvent the event
     */
    fun onSagaFailureEvent(sagaFailureEvent: SagaFailureEvent)

}
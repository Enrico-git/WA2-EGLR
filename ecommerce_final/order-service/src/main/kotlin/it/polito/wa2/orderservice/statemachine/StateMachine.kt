package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.domain.ProductLocation
import it.polito.wa2.orderservice.domain.RedisStateMachine
import it.polito.wa2.orderservice.domain.Transition
import kotlinx.coroutines.Job
import org.springframework.context.ApplicationEvent

interface StateMachine {

    /**
     * Get the transition based on current state and the firing event
     * @param event the State machine event
     * @return the transition if it exists
     */
    suspend fun getTransition(event: StateMachineEvents? = null): Transition?


        /**
     * Start the state machine
     */
    suspend fun start()

    /**
     * Fire an application event. Used to know when a machine changes state
     * @param event
     */
    suspend fun fireEvent(event: ApplicationEvent)

    /**
     * Perform next step in state machine based on event
     * @param event
     * @param productsLocation
     */
    suspend fun nextStateAndFireEvent(event: StateMachineEvents, productsLocation: Set<ProductLocation>? = null)

    /**
     * Resume the state machine in case the service crashed
     */
    suspend fun resume()

    /**
     * Perform backup of state machine status into redis. Since
     * Redis set doesnt support replace we have to remove and then insert
     * @param old the old state machine to remove
     * @param new the new state machine to insert
     * @return a coroutine job
     */
    suspend fun backup(oldSM: RedisStateMachine, newSM : RedisStateMachine): Job
}
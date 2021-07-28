package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.domain.ProductLocation
import it.polito.wa2.orderservice.dto.ProductDTO
import java.math.BigDecimal

interface StateMachineBuilder {

    /**
     * Set the initial state of the machine
     * @param source the state machine state
     * @return the same instance
     */
    fun initialState(source: StateMachineStates): StateMachineBuilder

    /**
     * Set the final state of the machine
     * @param final the state machine state
     * @return the same instance
     */
    fun finalState(final: StateMachineStates): StateMachineBuilder

    /**
     * Set the current state of the machine
     * @param newState the state machine state
     * @return the same instance
     */
    fun state(newState: StateMachineStates?): StateMachineBuilder

    /**
     * Set the id of the machine
     * @param id
     * @return the same instance
     */
    fun id(newId: String): StateMachineBuilder

    /**
     * Set the customer email of the machine
     * @param email
     * @return the same instance
     */
    fun customerEmail(email: String): StateMachineBuilder

    /**
     * Set the set of products of the machine
     * @param newProducts the set of products
     * @return the same instance
     */
    fun products(newProducts: Set<ProductDTO>?): StateMachineBuilder

    /**
     * Set the set of products location of the machine
     * @param newProductsWarehouseLocation the set of products locations
     * @return the same instance
     */
    fun productsWarehouseLocation(newProductsWarehouseLocation: Set<ProductLocation>?): StateMachineBuilder

    /**
     * Set the amount the user has to pay or be refunded of the machine
     * @param newAmount
     * @return the same instance
     */
    fun amount(newAmount: BigDecimal): StateMachineBuilder

    /**
     * Set the jwt of the machine
     * @param token
     * @return the same instance
     */
    fun auth(token: String): StateMachineBuilder

    /**
     * Set the source of a transition of the machine
     * @param source the transition starting state
     * @return the same instance
     */
    fun source(source: StateMachineStates): StateMachineBuilder

    /**
     * Set the target of a transition of the machine
     * @param target the transition target state
     * @return the same instance
     */
    fun target(target: StateMachineStates): StateMachineBuilder

    /**
     * Set the event of a transition of the machine
     * @param event the event fired during the transition
     * @return the same instance
     */
    fun event(event: StateMachineEvents): StateMachineBuilder

    /**
     * Set if the transition is a rolling back type transition of the machine
     * @param value true means that when fired the state machine is rolling back
     * @return the same instance
     */
    fun isRollingBack(value: Boolean): StateMachineBuilder

    /**
     * Set the type of transition
     * @param value true means state machine is waiting for an event, false the state machine needs to do an action
     * @return the same instance
     */
    fun isPassive(value: Boolean): StateMachineBuilder

    /**
     * Set if the state machine has failed
     * @param newFailed
     * @return the same instance
     */
    fun failed(newFailed: Boolean?): StateMachineBuilder

    /**
     * Set if the state machine has finished
     * @param newCompleted
     * @return the same instance
     */
    fun completed(newCompleted: Boolean?): StateMachineBuilder

    /**
     * Add an action to a transition
     * @param action a function
     * @return the same instance
     */
    fun action(action: (() -> Any?)?): StateMachineBuilder

    /**
     * Add a new empty transition
     * @return the same instance
     */
    fun and(): StateMachineBuilder

    /**
     * Build the State machine
     * @return the state machine instance
     */
    fun build(): StateMachineImpl
}
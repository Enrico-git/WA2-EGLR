package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.domain.ProductLocation
import it.polito.wa2.orderservice.domain.Transition
import it.polito.wa2.orderservice.dto.ProductDTO
import org.bson.types.ObjectId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StateMachineBuilder(private val applicationEventPublisher: ApplicationEventPublisher){
    lateinit var initialState: StateMachineStates
    lateinit var finalState: StateMachineStates
    lateinit var id: String
    lateinit var customerEmail: String
    lateinit var auth: String
    lateinit var amount: BigDecimal
    var products: Set<ProductDTO>? = null
    var productsWarehouseLocation: Set<ProductLocation>? = null
    var transitions: MutableList<Transition> = mutableListOf(Transition(null, null, null, null))

    fun initialState(source: StateMachineStates): StateMachineBuilder{
        initialState = source
        return this
    }
    fun finalState(final: StateMachineStates): StateMachineBuilder{
        finalState = final
        return this
    }

    fun id(newId: String): StateMachineBuilder{
        id = newId
        return this
    }

    fun customerEmail(email: String): StateMachineBuilder{
        customerEmail = email
        return this
    }

    fun products(newProducts: Set<ProductDTO>): StateMachineBuilder{
        products = newProducts
        return this
    }

    fun productsWarehouseLocation(newProductsWarehouseLocation: Set<ProductLocation>): StateMachineBuilder{
        productsWarehouseLocation = newProductsWarehouseLocation
        return this
    }

    fun amount(newAmount: BigDecimal): StateMachineBuilder{
        amount = newAmount
        return this
    }

    fun auth(token: String): StateMachineBuilder{
        auth = token
        return this
    }

    fun source(source: StateMachineStates): StateMachineBuilder{
        transitions.last().source = source
        return this
    }

    fun target(target: StateMachineStates): StateMachineBuilder{
        transitions.last().target = target
        return this
    }

    fun event(event: StateMachineEvents): StateMachineBuilder {
        transitions.last().event = event
        return this
    }

    fun action(action: (() -> Any?)?): StateMachineBuilder{
        transitions.last().action = action
        return this
    }

    fun and(): StateMachineBuilder {
        transitions.add(Transition(null, null, null, null))
        return this
    }

    fun build() = StateMachine(initialState,
        finalState,
        transitions,
        null,
        id,
        false,
    false,
        customerEmail,
        amount,
        products,
        productsWarehouseLocation,
        auth,
        applicationEventPublisher
    )

}

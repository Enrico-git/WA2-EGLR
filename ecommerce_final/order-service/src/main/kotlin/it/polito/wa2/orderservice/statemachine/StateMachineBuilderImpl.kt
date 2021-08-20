package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.domain.Transition
import it.polito.wa2.orderservice.dto.ProductDTO
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.logging.Logger

@Component
class StateMachineBuilderImpl(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val redisStateMachineRepository: RedisStateMachineRepository,
    private val logger: Logger
    )
    : StateMachineBuilder
{
    lateinit var initialState: StateMachineStates
    lateinit var finalState: StateMachineStates
    lateinit var id: String
    lateinit var customerEmail: String
    lateinit var auth: String
    lateinit var amount: BigDecimal
    var failed: Boolean? = false
    var shippingAddress: String? = null
    var completed: Boolean? = false
    var state: StateMachineStates? = null
    var products: Set<ProductDTO>? = null
    var transitions: MutableList<Transition> = mutableListOf(Transition(null, null, null, false, false, null))

    override fun initialState(source: StateMachineStates): StateMachineBuilder{
        initialState = source
        return this
    }
    override fun finalState(final: StateMachineStates): StateMachineBuilder{
        finalState = final
        return this
    }

    override fun state(newState: StateMachineStates?): StateMachineBuilder{
        state = newState
        return this
    }

    override fun id(newId: String): StateMachineBuilder{
        id = newId
        return this
    }

    override fun customerEmail(email: String): StateMachineBuilder{
        customerEmail = email
        return this
    }

    override fun shippingAddress(newAddress: String?): StateMachineBuilder{
        shippingAddress = newAddress
        return this
    }

    override fun products(newProducts: Set<ProductDTO>?): StateMachineBuilder{
        products = newProducts
        return this
    }

    override fun amount(newAmount: BigDecimal): StateMachineBuilder{
        amount = newAmount
        return this
    }

    override fun auth(token: String): StateMachineBuilder{
        auth = token
        return this
    }

    override fun source(source: StateMachineStates): StateMachineBuilder{
        transitions.last().source = source
        return this
    }

    override fun target(target: StateMachineStates): StateMachineBuilder{
        transitions.last().target = target
        return this
    }

    override fun event(event: StateMachineEvents): StateMachineBuilder {
        transitions.last().event = event
        return this
    }

    override fun isRollingBack(value: Boolean): StateMachineBuilder {
        transitions.last().isRollingBack = value
        return this
    }

    override fun isPassive(value: Boolean): StateMachineBuilder {
        transitions.last().isPassive = value
        return this
    }

    override fun failed(newFailed: Boolean?): StateMachineBuilder{
        failed = newFailed
        return this
    }
    override fun completed(newCompleted: Boolean?): StateMachineBuilder{
        completed = newCompleted
        return this
    }

    override fun action(action: (() -> Any?)?): StateMachineBuilder{
        transitions.last().action = action
        return this
    }

    override fun and(): StateMachineBuilder {
        transitions.add(Transition(null, null, null, false, false, null))
        return this
    }

    override fun build() = StateMachineImpl(initialState,
        finalState,
        transitions,
        state,
        id,
        false,
    false,
        customerEmail,
        shippingAddress,
        amount,
        products,
        auth,
        applicationEventPublisher,
        redisStateMachineRepository,
        logger
    )

}

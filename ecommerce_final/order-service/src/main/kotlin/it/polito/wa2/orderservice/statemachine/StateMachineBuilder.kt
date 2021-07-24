package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.domain.Transition
import org.bson.types.ObjectId
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class StateMachineBuilder(private val applicationEventPublisher: ApplicationEventPublisher){
    lateinit var initialState: String
    lateinit var finalState: String
    lateinit var id: String
    lateinit var customerEmail: String
    lateinit var auth: String
    var amount: BigDecimal? = null
    var transitions: MutableList<Transition> = mutableListOf(Transition(null, null, null, null))

    fun initialState(source: String): StateMachineBuilder{
        initialState = source
        return this
    }
    fun finalState(final: String): StateMachineBuilder{
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

    fun amount(newAmount: BigDecimal): StateMachineBuilder{
        amount = newAmount
        return this
    }

    fun auth(token: String): StateMachineBuilder{
        auth = token
        return this
    }

    fun source(source: String): StateMachineBuilder{
        transitions.last().source = source
        return this
    }

    fun target(target: String): StateMachineBuilder{
        transitions.last().target = target
        return this
    }

    fun event(event: String): StateMachineBuilder {
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
        auth,
        applicationEventPublisher
    )

}

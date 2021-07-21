package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.domain.Transition
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class StateMachineBuilder(private val applicationEventPublisher: ApplicationEventPublisher){
    lateinit var initialState: String
    lateinit var finalState: String
    lateinit var id: String
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

    fun build() = StateMachine(initialState, finalState, transitions, null, id, applicationEventPublisher)

}

package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.domain.Transition
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@Scope("prototype")
class StateMachine(val initialState: String = "",
                   val finalState: String = "",
                   val transitions: MutableList<Transition>,
                   var state: String? = null,
                   val id: String = "",
                   var failed: Boolean? = false,
                   var completed: Boolean? = false,
                   val customerEmail: String,
                   val amount: BigDecimal? = null,
                   val auth: String,
                   private val applicationEventPublisher: ApplicationEventPublisher
) {
    init {
        println("SM INITIATED")
    }
    fun start(): Boolean{
        state = initialState
        return true
    }

    suspend fun fireEvent(event: ApplicationEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    suspend fun send(event: String): Boolean {
        val transition = transitions.find{it.source == state && it.event == event}
        println("ID: $id, state $state, event $event")
        state = transition!!.target

        when (state) {
            finalState -> {
                fireEvent(StateMachineEvent(this, "$id-$event" ))
                fireEvent(SagaFinishedEvent(this))
            }
            initialState -> {
                fireEvent(StateMachineEvent(this, "$id-$event" ))
                fireEvent(SagaFailureEvent(this))
            }
            else -> {
                fireEvent(StateMachineEvent(this, "$id-$event" ))
            }
        }

        transition.action?.invoke()
        return true
    }

}
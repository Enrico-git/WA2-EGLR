package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.domain.Transition
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
class StateMachine(val initialState: String,
                   val finalState: String,
                   val transitions: MutableList<Transition>,
                   var state: String?,
                   val id: String,
                   private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun start(): Boolean{
        state = initialState
        return true
    }

    suspend fun fireEvent(event: ApplicationEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    suspend fun send(event: String): Boolean {
        val transition = transitions.find{it.source == state && it.event == event}
        state = transition!!.target

        when (state) {
            finalState -> {
//                fireEvent(KafkaResponseReceivedEventInResponseTo(this, "reserve_products"))
//                fireEvent(KafkaResponseReceivedEventInResponseTo(this, "payment_request"))
                fireEvent(StateMachineEvent(this, "$id-$event" ))
                fireEvent(SagaFinishedEvent(this))
            }
            initialState -> {
//                fireEvent(KafkaResponseReceivedEventInResponseTo(this, "reserve_products"))
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
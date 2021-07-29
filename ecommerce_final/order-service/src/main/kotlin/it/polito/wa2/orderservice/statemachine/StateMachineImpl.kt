package it.polito.wa2.orderservice.statemachine

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.domain.ProductLocation
import it.polito.wa2.orderservice.domain.RedisStateMachine
import it.polito.wa2.orderservice.domain.Transition
import it.polito.wa2.orderservice.domain.toRedisStateMachine
import it.polito.wa2.orderservice.dto.ProductDTO
import it.polito.wa2.orderservice.events.SagaFailureEvent
import it.polito.wa2.orderservice.events.SagaFinishedEvent
import it.polito.wa2.orderservice.events.StateMachineEvent
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@Scope("prototype")
class StateMachineImpl(val initialState: StateMachineStates,
                       val finalState: StateMachineStates,
                       val transitions: MutableList<Transition>,
                       var state: StateMachineStates?,
                       val id: String = "",
                       var failed: Boolean? = false,
                       var completed: Boolean? = false,
                       val customerEmail: String,
                       val amount: BigDecimal,
                       val products: Set<ProductDTO>? = null,
                       var productsWarehouseLocation: Set<ProductLocation>? = null,
                       val auth: String,
                       val applicationEventPublisher: ApplicationEventPublisher,
                       val redisStateMachineRepository: RedisStateMachineRepository

) : StateMachine{

    override suspend fun start(){
        state = initialState
        this.nextStateAndFireEvent(transitions.find { it.source == initialState }?.event!!)
    }

    override suspend fun fireEvent(event: ApplicationEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override suspend fun nextStateAndFireEvent(event: StateMachineEvents) {
        val transition = transitions.find{it.source == state && it.event == event}
        println("ID: $id, state $state, event $event")

        if (transition == null)
            return

        val oldSM = this.toRedisStateMachine()

        state = transition.target
        if ( failed == false && transition.isRollingBack)
            failed = true

        when (state) {
            finalState -> {
                fireEvent(StateMachineEvent(this, event ))
                fireEvent(SagaFinishedEvent(this))
                completed = true
            }
            initialState -> {
                fireEvent(StateMachineEvent(this, event ))
                fireEvent(SagaFailureEvent(this))
                completed = true
            }
            else -> {
                fireEvent(StateMachineEvent(this, event ))
            }
        }

        transition.action?.invoke()

        backup(oldSM, this.toRedisStateMachine())
    }

    override suspend fun resume(){
        if (this.failed == true)
            return
        else {
            val transition = transitions.find { it.source == state && !it.isRollingBack } ?: return
            if (transition.isPassive)
                return
            else
                this.nextStateAndFireEvent(transition.event!!)
        }
    }

    override suspend fun backup(old: RedisStateMachine, new :RedisStateMachine) = CoroutineScope(Dispatchers.IO).launch{
        redisStateMachineRepository.remove(old)
        redisStateMachineRepository.add(new)
    }

    override fun toString(): String {
        return "[ID: $id, STATE: $state, INITIAL_STATE: $initialState, FINAL_STATE: $finalState, FAILED: $failed, COMPLETED: $completed"
    }
}
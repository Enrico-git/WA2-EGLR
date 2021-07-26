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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
@Scope("prototype")
class StateMachine(val initialState: StateMachineStates,
                   val finalState: StateMachineStates,
                   val transitions: MutableList<Transition>,
                   var state: StateMachineStates? = null,
                   val id: String = "",
                   var failed: Boolean? = false,
                   var completed: Boolean? = false,
                   val customerEmail: String,
                   val amount: BigDecimal,
                   val products: Set<ProductDTO>? = null,
                   val productsWarehouseLocation: Set<ProductLocation>? = null,
                   val auth: String,
                   val applicationEventPublisher: ApplicationEventPublisher,
                   val redisStateMachineRepository: RedisStateMachineRepository

) {

    fun start(): Boolean{
        state = initialState
        return true
    }

    suspend fun fireEvent(event: ApplicationEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    suspend fun send(event: StateMachineEvents): Boolean {
        val transition = transitions.find{it.source == state && it.event == event}
        println("ID: $id, state $state, event $event")

        if (transition == null)
            return false

        val oldSM = this.toRedisStateMachine()

        state = transition.target

        when (state) {
            finalState -> {
                fireEvent(StateMachineEvent(this, "$id-$event" ))
                fireEvent(SagaFinishedEvent(this))
                completed = true
            }
            initialState -> {
                fireEvent(StateMachineEvent(this, "$id-$event" ))
                fireEvent(SagaFailureEvent(this))
                failed = true
            }
            else -> {
                fireEvent(StateMachineEvent(this, "$id-$event" ))
            }
        }

        transition.action?.invoke()

        backup(oldSM)
        return true
    }

    @Transactional
    suspend fun backup(old: RedisStateMachine, new :RedisStateMachine = this.toRedisStateMachine()) = CoroutineScope(Dispatchers.IO).launch{
        redisStateMachineRepository.remove(old)
        redisStateMachineRepository.add(new)
    }

}
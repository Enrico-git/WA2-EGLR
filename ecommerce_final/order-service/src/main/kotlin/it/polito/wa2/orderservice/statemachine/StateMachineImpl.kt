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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.logging.Logger

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
                       val redisStateMachineRepository: RedisStateMachineRepository,
                       val logger: Logger,
                       ) : StateMachine
{

    override suspend fun getTransition(event: StateMachineEvents?): Transition? {
        return if (event != null)
                transitions.find { it.source == state && it.event == event }
            else
                transitions.find { it.source == state }
    }
    override suspend fun start(){
        state = initialState
        getTransition()?.event?.let { this.nextStateAndFireEvent(it) }
    }

    override suspend fun fireEvent(event: ApplicationEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    
    override suspend fun nextStateAndFireEvent(event: StateMachineEvents, productsLocation: Set<ProductLocation>?) {

        val transition = getTransition(event) ?: return

        println("ID: $id, state $state, event $event")

        val oldSM = this.toRedisStateMachine()

        state = transition.target

        failed = if (failed == false && transition.isRollingBack) true else failed

        if (event == StateMachineEvents.RESERVE_PRODUCTS_OK  || event == StateMachineEvents.ABORT_PRODUCTS_RESERVATION_OK)
            productsWarehouseLocation = productsLocation

        fireEvent(StateMachineEvent(this, event ))

        if (state == finalState){
            fireEvent(SagaFinishedEvent(this))
            completed = true
        } else if (state == initialState){
            fireEvent(SagaFailureEvent(this))
            completed = true
        }

        backup(oldSM, this.toRedisStateMachine())

        transition.action?.invoke()

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

    override suspend fun backup(oldSM: RedisStateMachine, newSM :RedisStateMachine) = CoroutineScope(Dispatchers.IO).launch{
        try{
            redisStateMachineRepository.remove(oldSM)
            redisStateMachineRepository.add(newSM)
            return@launch
        } catch (e: Exception) {
            logger.severe("Cant backup new sm $newSM over old sm $oldSM")
        }
    }

    override fun toString(): String {
        return "[ID: $id, STATE: $state FAILED: $failed, COMPLETED: $completed"
    }
}
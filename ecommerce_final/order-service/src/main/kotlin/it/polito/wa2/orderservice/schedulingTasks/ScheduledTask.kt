package it.polito.wa2.orderservice.schedulingTasks

import it.polito.wa2.orderservice.statemachine.toRedisStateMachine
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.ConcurrentReferenceHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

@Component
@EnableScheduling
class ScheduledTask(
    private val jobs: ConcurrentReferenceHashMap<String, Job>,
    private val logger: Logger,
    private val redisStateMachineRepository: RedisStateMachineRepository
) {
    /**
     * Method to get a list of Prototype Beans without dependency injection
     * You need to use the lookup annotation otherwise it will initialize a new list for every injection
     */
    @Lookup
    @Lazy
    fun getListOfStateMachine(): ConcurrentHashMap<String,StateMachineImpl> {
        return null!!
    }

    /**
     * Scheduler to remove the finished sagas and order job from the list and free memory
     */
    @Scheduled(fixedRate = 1000*10) // 10s
    fun removeCompletedSagas() = CoroutineScope(Dispatchers.Default).launch{
        val sagas = getListOfStateMachine()
        logger.info("BEFORE REMOVED SAGAS: $sagas --- REMOVED JOBS: $jobs")
        sagas.values.removeIf {
            if(it.completed == true) {
                CoroutineScope(Dispatchers.IO).launch Job@{
                    var counter = 5
                    while (counter-- > 0)
                        try {
                            redisStateMachineRepository.remove(it.toRedisStateMachine())
                            return@Job
                        } catch (e: Exception){
                            delay(1000)
                        }
                    logger.severe("Could not remove state machine ${it.id} from redis")
                }
                return@removeIf true
            }
            return@removeIf false
        }

        logger.info("AFTER REMOVED SAGAS: $sagas --- REMOVED JOBS: $jobs")
        logger.info("SAGAS: $sagas --- JOBS: $jobs")
    }
}
package it.polito.wa2.orderservice.schedulingTasks

import it.polito.wa2.orderservice.domain.toRedisStateMachine
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import it.polito.wa2.orderservice.statemachine.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Lookup
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

@Component
@EnableScheduling
class ScheduledTask(
    private val jobs: ConcurrentHashMap<String, Job>,
    private val logger: Logger,
    private val redisStateMachineRepository: RedisStateMachineRepository
) {
    /**
     * Method to get a list of Prototype Beans without dependency injection
     * You need to use the lookup annotation otherwise it will initialize a new list for every injection
     */
    @Lookup
    @Lazy
    fun getListOfStateMachine(): ConcurrentHashMap<String,StateMachine> {
        return null!!
    }

    /**
     * Scheduler to remove the finished sagas and order job from the list and free memory
     */
    @Scheduled(fixedRate = 1000*10)
    fun removeCompletedSagasAndJobs() = CoroutineScope(Dispatchers.Default).launch{
        val sagas = getListOfStateMachine()
        logger.info("BEFORE REMOVED SAGAS: $sagas --- REMOVED JOBS: $jobs")
        sagas.values.removeIf {
            if(it.completed == true || it.failed == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    redisStateMachineRepository.remove(it.toRedisStateMachine())
                }
                return@removeIf true
            }
            return@removeIf false
        }

        jobs.values.removeIf{ it.isCancelled || it.isCompleted}
        logger.info("AFTER REMOVED SAGAS: $sagas --- REMOVED JOBS: $jobs")
        logger.info("SAGAS: $sagas --- JOBS: $jobs")
//        logger.info("SAGAS: ${sagas.get(0).completed}")
    }
}
package it.polito.wa2.orderservice.schedulingTasks

import it.polito.wa2.orderservice.domain.OrderJob
import it.polito.wa2.orderservice.statemachine.StateMachine
import kotlinx.coroutines.Job
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
@EnableScheduling
class ScheduledTask(
    private val sagas: MutableList<StateMachine>,
    private val jobs: MutableList<OrderJob>,
    private val logger: Logger
) {

    @Scheduled(fixedRate = 1000)
    fun removeCompletedSagasAndJobs() {
//        TODO FIX STATEMACHINE LIST
        logger.info("BEFORE REMOVED SAGAS: $sagas --- REMOVED JOBS: $jobs")
        val removedSagas = sagas.removeIf { it.completed == true || it.failed == true }
        val removedJobs = jobs.removeIf { it.second.isCancelled || it.second.isCompleted}
        logger.info("AFTER REMOVED SAGAS: $sagas --- REMOVED JOBS: $jobs")
        logger.info("SAGAS: $sagas --- JOBS: $jobs")
        logger.info("SAGAS: ${sagas.get(0).completed}")
    }
}
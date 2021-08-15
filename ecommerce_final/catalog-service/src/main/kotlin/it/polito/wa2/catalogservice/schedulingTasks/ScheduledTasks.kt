package it.polito.wa2.catalogservice.schedulingTasks

import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class ScheduledTasks(
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository
) {

    @Scheduled(fixedRate = 1000 * 60 * 60 * 12) //12 HOURS
    fun clearExpiredTokens() = CoroutineScope(Dispatchers.IO).launch{
        emailVerificationTokenRepository.deleteAllByExpiryDateBefore(Timestamp(System.currentTimeMillis()))
    }
}
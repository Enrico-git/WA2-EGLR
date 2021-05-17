package it.polito.ecommerce.schedulingTasks

import it.polito.ecommerce.repositories.EmailVerificationTokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class ScheduledTasks(
    private val emailVerificationTokenRepository: EmailVerificationTokenRepository
) {

    @Scheduled(fixedRate = 1000 * 60 * 60 * 12) //12 HOURS
    fun clearExpiredTokens() {
        emailVerificationTokenRepository.deleteAllByExpiryDate(Timestamp(System.currentTimeMillis()))
    }
}
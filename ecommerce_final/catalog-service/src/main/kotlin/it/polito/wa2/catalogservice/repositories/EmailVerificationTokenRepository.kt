package it.polito.wa2.catalogservice.repositories

import it.polito.wa2.catalogservice.domain.EmailVerificationToken
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

interface EmailVerificationTokenRepository : CoroutineCrudRepository<EmailVerificationToken, Long> {
    suspend fun findByToken(token: String): EmailVerificationToken?

    @Transactional
    suspend fun deleteAllByExpiryDateBefore(actualTime: Timestamp)
}
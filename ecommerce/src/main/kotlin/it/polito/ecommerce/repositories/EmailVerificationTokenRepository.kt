package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.EmailVerificationToken
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import java.util.*
import javax.transaction.Transactional

interface EmailVerificationTokenRepository : CrudRepository<EmailVerificationToken, Long> {
    fun findByToken(token: String): Optional<EmailVerificationToken>

    @Transactional
    @Modifying
    @Query("delete from EmailVerificationToken e where e.expiryDate<?1")
    fun deleteAllByExpiryDate(actualTime: Timestamp)
}
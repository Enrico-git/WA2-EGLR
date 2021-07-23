package it.polito.wa2.catalogservice.repositories

import it.polito.wa2.catalogservice.domain.EmailVerificationToken
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.util.*

interface EmailVerificationTokenRepository : CoroutineCrudRepository<EmailVerificationToken, Long> {
    fun findByToken(token: String): Optional<EmailVerificationToken>

    @Transactional
    //@Modifying TODO set modifying because this query modifies the collection
    @Query("delete from EmailVerificationToken e where e.expiryDate<?1")
    fun deleteAllByExpiryDate(actualTime: Timestamp)
}
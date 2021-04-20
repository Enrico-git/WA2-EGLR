package it.polito.ecommerce.services

import it.polito.ecommerce.domain.EmailVerificationToken
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.repositories.EmailVerificationTokenRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class NotificationServiceImpl(
    private val notificationRepository: EmailVerificationTokenRepository,
)
    : NotificationService {
    override fun createEmailVerificationToken(user: User): String {
        // Check existence not needed because called by another service

        val notification = EmailVerificationToken(
            expiry_date = Timestamp(System.currentTimeMillis() + 1000*60*60) ,
            token = UUID.randomUUID().toString(),
            user = user
        )

        notificationRepository.save(notification)

        return notification.token
    }
}
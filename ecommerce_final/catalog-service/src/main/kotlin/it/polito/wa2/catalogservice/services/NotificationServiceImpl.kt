package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.EmailVerificationToken
import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*

@Service
class NotificationServiceImpl(
    private val notificationRepository: EmailVerificationTokenRepository,
    private val userRepository: UserRepository
) : NotificationService {
    override fun createEmailVerificationToken(user: User): String {
        val notification = EmailVerificationToken(
            expiryDate = Timestamp(System.currentTimeMillis() + 1000 * 60 * 60), //one hour
            token = UUID.randomUUID().toString(),
            user = user
        )
        notificationRepository.save(notification)
        return notification.token
    }
}
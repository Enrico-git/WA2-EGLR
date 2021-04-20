package it.polito.ecommerce.services

import it.polito.ecommerce.domain.EmailVerificationToken
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.repositories.EmailVerificationTokenRepository
import it.polito.ecommerce.repositories.UserRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class NotificationServiceImpl(
    private val notificationRepository: EmailVerificationTokenRepository,
    private val userRepository: UserRepository,
) : NotificationService {

    override fun createEmailVerificationToken(user: User): String {

        val notification = EmailVerificationToken(
            expiryDate = Timestamp(System.currentTimeMillis() + 1000*60*60), // 1 hour
            token = UUID.randomUUID().toString(),
            user = user
        )

        notificationRepository.save(notification)

        return notification.token
    }
}
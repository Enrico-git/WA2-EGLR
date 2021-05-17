package it.polito.ecommerce.services

import it.polito.ecommerce.domain.EmailVerificationToken
import it.polito.ecommerce.domain.User

interface NotificationService {
    fun createEmailVerificationToken(user: User): String
}
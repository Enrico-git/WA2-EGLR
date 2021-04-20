package it.polito.ecommerce.services

import it.polito.ecommerce.domain.User

interface NotificationService {

    fun createEmailVerificationToken(userID: User): String
}
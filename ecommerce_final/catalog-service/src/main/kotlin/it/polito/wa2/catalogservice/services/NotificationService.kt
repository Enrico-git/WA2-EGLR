package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.User

interface NotificationService {
    fun createEmailVerificationToken(user: User): String
}
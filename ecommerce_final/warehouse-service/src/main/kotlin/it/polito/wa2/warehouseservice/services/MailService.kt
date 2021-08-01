package it.polito.wa2.warehouseservice.services

interface MailService {
    suspend fun sendMessage(toMail: String, subject: String, mailBody: String)
    suspend fun notifyAdmin(subject: String, productID: String)
}
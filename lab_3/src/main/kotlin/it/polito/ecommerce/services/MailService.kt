package it.polito.ecommerce.services


interface MailService {
    fun sendMessage(toMail: String, subject: String, mailBody: String)
}
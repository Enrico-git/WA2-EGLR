package it.polito.wa2.warehouseservice.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MailServiceImpl(
        private val mailSender: JavaMailSender,
        @Value("\${spring.mail.username}") private val adminEmail: String
): MailService {
    override suspend fun sendMessage(toMail: String, subject: String, mailBody: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        helper.setTo(toMail)
        helper.setSubject(subject)
        helper.setText(mailBody, true)
        mailSender.send(message)
    }

    override suspend fun notifyAdmin(subject: String, productID: String) {
        val body = "The product $productID has overcome the alarm threshold. Recharge it!"
        sendMessage(adminEmail, subject, body)
    }
}
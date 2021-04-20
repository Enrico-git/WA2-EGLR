package it.polito.ecommerce.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailServiceImpl(
    private val mailSender : JavaMailSender
) : MailService{
    override fun sendMessage(toMail: String, subject: String, mailBody: String) {
        val message = SimpleMailMessage()
        message.setTo(toMail)
        message.setSubject(subject)
        message.setText(mailBody)
        mailSender.send(message)
    }
}
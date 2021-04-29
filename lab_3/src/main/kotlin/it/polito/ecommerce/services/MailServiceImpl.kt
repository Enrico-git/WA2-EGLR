package it.polito.ecommerce.services

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailServiceImpl(
    private val mailSender: JavaMailSender
) : MailService
{

    override fun sendMessage(toMail: String, subject: String, mailBody: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        helper.setTo(mailBody)
        helper.setSubject(subject)
        helper.setText(mailBody,true)

        mailSender.send(message)
    }
}
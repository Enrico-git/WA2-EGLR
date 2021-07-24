package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.common.OrderStatus
import kotlinx.coroutines.delay
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class MailServiceImpl (
    private val mailSender: JavaMailSender,
    @Value("\${application.admin.email}") private val adminEmail: String

) : MailService {
    override suspend fun sendMessage(toMail: String, subject: String, mailBody: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "utf-8")
        helper.setTo(toMail)
        helper.setSubject(subject)
        helper.setText(mailBody, true)
        mailSender.send(message)
    }

    override suspend fun notifyCustomer(toMail: String, subject: String, orderID: String, type: String, status: OrderStatus?) {
        lateinit var body: String
        when (type){
            "ISSUED" -> body = "The Order $orderID has been ISSUED"
            "CANCELED" -> body = "The Order $orderID has been CANCELLED"
            "ISSUE_FAILED" -> body = "The creation of order $orderID has FAILED"
            "CANCELLATION_FAILED" -> body = "The cancellation of $orderID has FAILED. Please try again later"
            "UPDATE" -> body = "The Order $orderID status has been updated to $status"
        }
        sendMessage(toMail, subject, body)
    }

    override suspend fun notifyAdmin(subject: String, orderID: String, type: String, status: OrderStatus?) {
        lateinit var body: String
        when (type){
            "ISSUED" -> body = "The Order $orderID has been ISSUED"
            "CANCELED" -> body = "The Order $orderID has been CANCELLED"
            "ISSUE_FAILED" -> body = "The creation of order $orderID has FAILED"
            "CANCELLATION_FAILED" -> body = "The cancellation of $orderID has FAILED"
            "UPDATE" -> body = "The Order $orderID status has been updated to $status"
            "ERROR" -> body = "There was an error aborting the products reservation for order $orderID"
        }
        sendMessage(adminEmail, subject, body)
    }
}
package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.common.EmailType
import it.polito.wa2.orderservice.common.OrderStatus
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

    override suspend fun notifyCustomer(toMail: String, subject: String, orderID: String, type: EmailType, status: OrderStatus?) {
        val body = when (type){
            EmailType.ISSUED -> "The Order $orderID has been ISSUED"
            EmailType.CANCELED -> "The Order $orderID has been CANCELLED"
            EmailType.ISSUE_FAILED -> "The creation of order $orderID has FAILED"
            EmailType.CANCELLATION_FAILED -> "The cancellation of $orderID has FAILED. Please try again later"
            EmailType.UPDATE -> "The Order $orderID status has been updated to $status"
            else -> return
        }
        sendMessage(toMail, subject, body)
    }

    override suspend fun notifyAdmin(subject: String, orderID: String, type: EmailType, status: OrderStatus?) {
        val body = when (type){
            EmailType.ISSUED -> "The Order $orderID has been ISSUED"
            EmailType.CANCELED -> "The Order $orderID has been CANCELLED"
            EmailType.ISSUE_FAILED -> "The creation of order $orderID has FAILED"
            EmailType.CANCELLATION_FAILED -> "The cancellation of $orderID has FAILED"
            EmailType.UPDATE -> "The Order $orderID status has been updated to $status"
            EmailType.ABORT_PRODUCT_ERROR -> "There was an error aborting the products reservation for order $orderID"
            EmailType.UPDATE_STATUS_ERROR -> "There was an error updating the status of order $orderID to $status"
        }
        sendMessage(adminEmail, subject, body)
    }
}
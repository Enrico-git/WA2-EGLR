package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.common.EmailType
import it.polito.wa2.orderservice.common.OrderStatus

interface MailService {
    suspend fun sendMessage(toMail: String, subject: String, mailBody: String)
    suspend fun notifyCustomer(toMail: String, subject: String, orderID: String, type: EmailType, status: OrderStatus? = null)
    suspend fun notifyAdmin(subject: String, orderID: String, type: EmailType, status: OrderStatus? = null)
}
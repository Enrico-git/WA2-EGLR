package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Transaction
import java.math.BigDecimal
import java.sql.Timestamp

data class TransactionDTO(
    val id: Long,
    val senderID: Long,
    val sender: String,
    val receiverID: Long,
    val receiver: String,
    val timestamp: Timestamp,
    val amount: BigDecimal
)

fun Transaction.toDTO(): TransactionDTO {
    return TransactionDTO(
        id = id!!,
        timestamp = timestamp,
        senderID = sender.id!!,
        sender = sender.customer.name + " " + sender.customer.surname,
        receiverID = receiver.id!!,
        receiver = receiver.customer.name + " " + receiver.customer.surname,
        amount = amount
    )
}
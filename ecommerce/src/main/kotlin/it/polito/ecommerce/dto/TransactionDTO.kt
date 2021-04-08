package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Transaction
import java.math.BigDecimal
import java.sql.Timestamp
import javax.validation.constraints.Min

data class TransactionDTO(
    val id: Long?,
    @field:Min(0, message = "the sender ID must be higher than zero")
    var senderID: Long?,
    @field:Min(0, message = "the receiver ID must be higher than zero")
    val receiverID: Long,
    val sender: String?,
    val receiver: String?,
    var timestamp: Timestamp?,
    @field:Min(0, message = "the transaction must be higher than zero")
    val amount: BigDecimal)

fun Transaction.toDTO() = TransactionDTO(
    id = id!!,
    senderID = sender.id!!,
    receiverID = receiver.id!!,
    sender = "${sender.customer.name} ${sender.customer.surname}",
    receiver = "${receiver.customer.name} ${receiver.customer.surname}",
    timestamp = timestamp,
    amount = amount
)
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
    var timestamp: Timestamp?,
    @field:Min(0, message = "the transaction must be higher than zero")
    val amount: BigDecimal)

fun Transaction.toDTO() = TransactionDTO(
    id = getId()!!,
    senderID = sender.getId()!!,
    receiverID = receiver.getId()!!,
    timestamp = timestamp,
    amount = amount
)
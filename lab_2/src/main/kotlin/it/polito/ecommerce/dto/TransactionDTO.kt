package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Transaction
import java.math.BigDecimal
import java.sql.Timestamp
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class TransactionDTO(
    val id: Long?,
    @field:Min(0, message = "the sender ID must be higher than 0")
    var senderID: Long?,
    @field:Min(0, message = "the receiver ID must be higher than 0")
    @field:NotNull(message = "the receiver must not be null")
    val receiverID: Long?,
    val timestamp: Timestamp?,
    @field:Min(0, message = "the transaction amount must be higher than 0")
    @field:NotNull(message = "the amount must not be null")
    val amount: BigDecimal?
)

fun Transaction.toDTO(): TransactionDTO {
    return TransactionDTO(
        id = getId()!!,
        timestamp = timestamp,
        senderID =sender.getId()!!,
        receiverID = receiver.getId()!!,
        amount = amount
    )
}
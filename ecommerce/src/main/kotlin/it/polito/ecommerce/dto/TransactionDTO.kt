package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Transaction
import java.math.BigDecimal
import java.sql.Timestamp
import javax.validation.constraints.Min

data class TransactionDTO(
    val id: Long?,
    @field:Min(0, message="sender ID must be higher than 0")
    var senderID: Long?,
    var sender: String?,
    @field:Min(0, message="receiver ID must be higher than 0")
    val receiverID: Long,
    val receiver: String?,
    val timestamp: Timestamp?,
    @field:Min(0, message="the transaction amount must be higher than 0")
    val amount: BigDecimal) {
}

fun Transaction.toDTO(): TransactionDTO {
    return TransactionDTO(
        id = id!!,
        senderID = sender.id!!,
        sender = "${sender.customer.name} ${sender.customer.surname}",
        receiverID = receiver.id!!,
        receiver = "${receiver.customer.name} ${receiver.customer.surname}",
        timestamp = timestamp,
        amount = amount
    )
}

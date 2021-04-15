package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Transaction
import java.math.BigDecimal
import java.sql.Timestamp
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class TransactionDTO(
    val id: Long?,
    @field:Min(0)
    var senderID: Long?,
    @field:Min(0)
    @field:NotNull
    val receiverID: Long?,
    var timestamp: Timestamp?,
    @field:Min(0)
    @field:NotNull
    val amount: BigDecimal?)

fun Transaction.toDTO() = TransactionDTO(
    id = getId()!!,
    senderID = sender.getId()!!,
    receiverID = receiver.getId()!!,
    timestamp = timestamp,
    amount = amount
)
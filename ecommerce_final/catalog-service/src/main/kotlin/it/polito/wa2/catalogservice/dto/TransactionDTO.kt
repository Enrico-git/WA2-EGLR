package it.polito.wa2.catalogservice.dto

import java.math.BigDecimal
import java.sql.Timestamp
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class TransactionDTO(
    var id: String?,
    var timestamp: Timestamp?,
    var walletID: String?,
    @field:Min(0, message = "Amount must be >= 0")
    val amount: BigDecimal,
    var description: String?,
    @field:NotNull(message = "OrderID must not be null")
    val orderID: String,
){
    override fun toString(): String {
        return "id: $id, timestamp: $timestamp, walletID: $walletID, " +
                "amount = $amount, description: $description, orderID: $orderID"
    }
}
package it.polito.wa2.walletservice.dto

import it.polito.wa2.walletservice.entities.Transaction
import it.polito.wa2.walletservice.common.TransactionDescription
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.sql.Timestamp

class TransactionDTO(
    var id: String?,
    var timestamp: Timestamp?,
    var walletID: String?,
    val amount: BigDecimal,
    var description: String?,
    val reason: String?,
){
    override fun toString(): String {
        return "id: $id, timestamp: $timestamp, walletID: $walletID, " +
                "amount = $amount, description: $description, orderID: $reason"
    }
}

fun TransactionDTO.toEntity() = Transaction(
    id = null,
    timestamp = timestamp!!,
    walletID = ObjectId(walletID),
    amount = amount,
    description = TransactionDescription.valueOf(description!!),
    reason = if (reason!= null) ObjectId(reason) else null
)

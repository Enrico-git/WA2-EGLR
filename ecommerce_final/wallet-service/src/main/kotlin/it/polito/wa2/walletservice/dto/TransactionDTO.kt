package it.polito.wa2.walletservice.dto

import it.polito.wa2.walletservice.entities.Transaction
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.sql.Timestamp

class TransactionDTO(
    var id: String?,
    var timestamp: Timestamp?,
    var walletID: String?,
    val amount: BigDecimal,
    val orderID: String,
){
    override fun toString(): String {
        return "{id: $id, timestamp: $timestamp, walletID: $walletID, amount: $amount, orderID: $orderID}"
    }
}

//createTransaction receive DTO and to perform 'save' need an entity
fun TransactionDTO.toEntity() = Transaction(
    id = null,
    timestamp = timestamp!!,
    walletID = ObjectId(walletID),
    amount = amount,
    orderID = ObjectId(orderID)
)

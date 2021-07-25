package it.polito.wa2.walletservice.dto

import it.polito.wa2.walletservice.entities.Transaction
import it.polito.wa2.walletservice.entities.TransactionDescription
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.sql.Timestamp

class TransactionDTO(
    var id: String?,
    var timestamp: Timestamp?,
    var walletID: String?,
    val amount: BigDecimal,
    val description: String,
    val orderID: String,
)

//createTransaction receive DTO and to perform 'save' need an entity
fun TransactionDTO.toEntity() = Transaction(
    id = null,
    timestamp = timestamp!!,
    walletID = ObjectId(walletID),
    amount = amount,
    description = TransactionDescription.valueOf(description),
    orderID = ObjectId(orderID)
)

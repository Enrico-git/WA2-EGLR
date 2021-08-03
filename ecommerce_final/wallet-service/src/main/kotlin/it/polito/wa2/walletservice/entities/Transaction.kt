package it.polito.wa2.walletservice.entities

import it.polito.wa2.walletservice.dto.TransactionDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.sql.Timestamp


enum class TransactionDescription {
    REFUND, RECHARGE, PAYMENT
}

@Document(collection = "transactions")
data class Transaction (
    @Id
    var id: ObjectId?,
    val timestamp: Timestamp,
    val walletID: ObjectId,
    var amount: BigDecimal = BigDecimal(0.0),
    val description: TransactionDescription,
    val orderID: ObjectId, //also recharge reference
)

fun Transaction.toDTO() = TransactionDTO(
    id = id?.toHexString(),
    timestamp = timestamp,
    walletID = walletID.toString(),
    amount = amount,
    description = description.name,
    orderID = orderID.toString()
)



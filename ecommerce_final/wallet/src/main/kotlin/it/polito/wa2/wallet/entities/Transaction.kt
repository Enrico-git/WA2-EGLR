package it.polito.wa2.wallet.entities

import it.polito.wa2.wallet.dto.TransactionDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.sql.Timestamp

@Document(collection = "transactions")
data class Transaction (
    @Id
    var id: ObjectId?,
    val timestamp: Timestamp,
    val walletID: ObjectId,
    var amount: BigDecimal = BigDecimal(0.0),
    val orderID: ObjectId, //also recharge reference in case of refund //TODO ObjectId or Order (aggregation)
)

fun Transaction.toDTO() = TransactionDTO(
    id = id?.toHexString(),
    timestamp = timestamp,
    walletID = walletID.toString(),
    amount = amount,
    orderID = orderID.toString()
)



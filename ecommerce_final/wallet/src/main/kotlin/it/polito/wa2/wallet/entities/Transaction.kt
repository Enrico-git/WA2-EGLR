package it.polito.wa2.wallet.entities

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "transactions")
data class Transaction (
    @Id
    val id: ObjectId?,
    var amount: BigDecimal = BigDecimal(0.0),
    val orderID: ObjectId, //also recharge reference in case of refund //TODO ObjectId or Order?
)



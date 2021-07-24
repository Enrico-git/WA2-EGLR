package it.polito.wa2.orderservice.domain

import org.bson.types.ObjectId
import java.math.BigDecimal

data class PaymentRequest(
    val orderID: String,
    val amount: BigDecimal,
    val token: String
)

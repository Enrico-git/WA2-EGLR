package it.polito.wa2.orderservice.dto

import org.bson.types.ObjectId
import java.math.BigDecimal

data class PaymentRequestDTO(
    val orderID: String,
    val amount: BigDecimal,
    val token: String
)

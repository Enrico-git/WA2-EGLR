package it.polito.wa2.orderservice.dto

import java.math.BigDecimal
import java.sql.Timestamp

data class PaymentRequestDTO(
    val orderID: String,
    val amount: BigDecimal,
    val token: String,
    val timestamp: Timestamp
)

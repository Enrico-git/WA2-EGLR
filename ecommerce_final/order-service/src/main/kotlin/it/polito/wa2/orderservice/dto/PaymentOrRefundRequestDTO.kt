package it.polito.wa2.orderservice.dto

import java.math.BigDecimal
import java.sql.Timestamp

data class PaymentOrRefundRequestDTO(
    val orderID: String,
    val amount: BigDecimal = BigDecimal.ZERO,
    val token: String,
    val timestamp: Timestamp
)

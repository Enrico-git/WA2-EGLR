package it.polito.wa2.walletservice.dto

import java.math.BigDecimal
import java.sql.Timestamp

data class KafkaPaymentRequestDTO (
    val orderID: String = "",
    val amount: BigDecimal = BigDecimal(0),
    val token: String = "",
    val timestamp: Timestamp = Timestamp(0)
)

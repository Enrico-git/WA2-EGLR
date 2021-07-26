package it.polito.wa2.walletservice.dto

import java.math.BigDecimal

data class KafkaPaymentRequestDTO (
    val orderID: String = "",
    val amount: BigDecimal = BigDecimal(0),
    val token: String = ""//from userID I will get walletID
)

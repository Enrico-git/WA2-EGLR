package it.polito.wa2.orderservice.dto

import java.sql.Timestamp

data class AbortProductReservationRequestDTO(
    val orderID: String,
    val timestamp: Timestamp
)

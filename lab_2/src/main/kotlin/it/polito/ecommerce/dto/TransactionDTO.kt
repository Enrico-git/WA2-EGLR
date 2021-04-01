package it.polito.ecommerce.dto

import java.sql.Timestamp

data class TransactionDTO(
    val id: Int,
    val sender: String,
    val receiver: String,
    val timestamp: Timestamp,
    val amount: Double
)
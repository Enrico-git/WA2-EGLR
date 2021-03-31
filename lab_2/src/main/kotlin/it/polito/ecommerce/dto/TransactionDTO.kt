package it.polito.ecommerce.dto

import java.sql.Timestamp

data class TransactionDTO(
    val id: Int,
    val sender: Int,
    val receiver: Int,
    val timestamp: Timestamp,
    val amount: Double
)
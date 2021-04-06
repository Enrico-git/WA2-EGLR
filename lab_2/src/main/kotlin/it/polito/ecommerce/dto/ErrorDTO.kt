package it.polito.ecommerce.dto

import java.sql.Timestamp

data class ErrorDTO(
    val timestamp: Timestamp,
    var status: Int? = null,
    val error: String
)
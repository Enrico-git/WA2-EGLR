package it.polito.ecommerce.warehouse.dto

import java.sql.Timestamp

data class ErrorDTO(
    val timestamp: Timestamp,
    var status: Int = 400,
    var error: String
)
package it.polito.ecommerce.dto

import java.sql.Timestamp

data class ErrorDTO(val timestamp: Timestamp,
                    val status: Int,
                    val error: String,
                    )
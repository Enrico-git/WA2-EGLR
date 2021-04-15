package it.polito.ecommerce.dto

import java.sql.Timestamp

data class ErrorDTO(var timestamp: Timestamp,
                    var status: Int = 400,
                    var error: String,
                    )
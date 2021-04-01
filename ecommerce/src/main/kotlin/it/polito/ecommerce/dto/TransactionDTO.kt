package it.polito.ecommerce.dto

import java.sql.Timestamp

data class TransactionDTO(val id: Int,
                          val timestamp: Timestamp,
                        val sender: String,
                        val receiver: String,
                        val amount: Double)
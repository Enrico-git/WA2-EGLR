package it.polito.ecommerce.dto

import java.sql.Timestamp

data class TransactionDTO(val sender: CustomerDTO,
                          val receiver: CustomerDTO,
                          val timestamp: Timestamp,
                          val amount: Double) {
}

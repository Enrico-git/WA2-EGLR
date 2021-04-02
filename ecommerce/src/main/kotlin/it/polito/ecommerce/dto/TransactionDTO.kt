package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Transaction
import java.math.BigDecimal
import java.sql.Timestamp

data class TransactionDTO(val id: Int,
                          val senderID: Int,
                          val receiverID: Int,
                        val sender: String,
                        val receiver: String,
                        val timestamp: Timestamp,
                        val amount: BigDecimal)

fun Transaction.toDTO() = TransactionDTO(id!!, sender.id!!, receiver.id!!,
    sender.customer.name + " " + sender.customer.surname,
  receiver.customer.name + " " + receiver.customer.surname,
    timestamp,  amount  )
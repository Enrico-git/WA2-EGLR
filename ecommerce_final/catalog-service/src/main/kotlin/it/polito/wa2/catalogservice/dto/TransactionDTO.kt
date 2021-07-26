package it.polito.wa2.catalogservice.dto

import java.math.BigDecimal
import java.sql.Timestamp

class TransactionDTO(
    var id: String?,
    var timestamp: Timestamp?,
    var walletID: String?,
    val amount: BigDecimal,
    val description: String,
    val orderID: String,
){
    override fun toString(): String {
        return "{id: $id, timestamp: $timestamp, walletID: $walletID, amount: $amount, orderID: $orderID}"
    }
}
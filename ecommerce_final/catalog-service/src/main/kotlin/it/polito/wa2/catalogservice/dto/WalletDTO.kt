package it.polito.wa2.catalogservice.dto

import java.math.BigDecimal

data class WalletDTO(
    val id: String?,
    val balance: BigDecimal,
    val userID: String, //TODO how to map nested object ? ObjectId or User
    //val transactions: Set<ObjectId> // ObjectId or Set<Transaction> ?
)
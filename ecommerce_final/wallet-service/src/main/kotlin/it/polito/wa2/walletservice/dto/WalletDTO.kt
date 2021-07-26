package it.polito.wa2.walletservice.dto

import it.polito.wa2.walletservice.entities.Wallet
import org.bson.types.ObjectId
import java.math.BigDecimal

data class WalletDTO(
    var id: String?,
    val balance: BigDecimal = BigDecimal(0),
    val userID: String,
    //val transactions: Set<String> // use specific end-point for retrieve walletTransactions!!!
)

fun WalletDTO.toEntity() = Wallet(
    id = null,
    balance = balance,
    userID = ObjectId(userID),
    //transactions = transactions.map { ObjectId(it) }.toSet()
)

package it.polito.wa2.wallet.dto

import org.bson.types.ObjectId
import java.math.BigDecimal

data class WalletDTO(
    val id: ObjectId,
    val balance: BigDecimal,
    val userID: ObjectId //TODO how to map nested object ?
)

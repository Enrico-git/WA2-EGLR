package it.polito.wa2.walletservice.entities

import it.polito.wa2.walletservice.dto.WalletDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "wallets")
data class Wallet (
    @Id
    val id: ObjectId?,
    var balance: BigDecimal = BigDecimal(0.0),
    val userID: ObjectId,
    //  00
    @Version
    val version: Long = 0 //needed for createTransaction cuncurrent
)

fun Wallet.toDTO() = WalletDTO(
    id = id!!.toHexString(),
    balance = balance,
    userID = userID.toHexString(),
)

// 00
// Please notice that the Wallet doesn't have the
// "var transactions: Set<ObjectId>?" proprieties since we assume that
// will be used the specific end-point for retrieve wallet Transactions!

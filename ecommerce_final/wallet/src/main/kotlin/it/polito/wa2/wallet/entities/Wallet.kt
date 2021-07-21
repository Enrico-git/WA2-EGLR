package it.polito.wa2.wallet.entities

import it.polito.wa2.wallet.dto.WalletDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@Document(collection = "wallets")
data class Wallet (
    @Id
    val id: ObjectId?,
    var balance: BigDecimal = BigDecimal(0.0),
    val userID: ObjectId, //TODO JWT uses User not Customer. Avoiding the use of Customer we loose name, surname, address
    //@Version
    //val version: Long = 0 //TODO Where do i need lock? performTransaction?
)

fun Wallet.toDTO() = WalletDTO(
    id = id!!,
    balance = balance,
    userID = userID
)

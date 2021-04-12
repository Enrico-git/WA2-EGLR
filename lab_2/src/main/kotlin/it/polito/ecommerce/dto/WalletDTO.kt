package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Wallet
import java.math.BigDecimal


data class WalletDTO(
    val id: Long,
    val balance: BigDecimal,
    val customerID: Long
)

fun Wallet.toDTO() = WalletDTO(
    id = getId()!!,
    balance = balance,
    customerID = customer.getId()!!
)
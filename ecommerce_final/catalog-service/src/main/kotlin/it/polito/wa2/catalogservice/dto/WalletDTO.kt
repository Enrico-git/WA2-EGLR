package it.polito.wa2.catalogservice.dto

import java.math.BigDecimal

data class WalletDTO(
    val id: String?,
    val balance: BigDecimal = BigDecimal(0),
    val userID: String
)
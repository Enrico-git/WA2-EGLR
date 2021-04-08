package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Wallet
import java.math.BigDecimal

data class WalletDTO(val id: Long,
                     val balance: BigDecimal,
                     val customer: CustomerDTO) {
}

fun Wallet.toDTO(): WalletDTO {
    return WalletDTO(
        id = id!!,
        balance = balance,
        customer = customer.toDTO()
    )
}

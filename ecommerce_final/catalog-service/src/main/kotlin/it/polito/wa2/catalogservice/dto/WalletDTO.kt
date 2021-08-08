package it.polito.wa2.catalogservice.dto

import java.math.BigDecimal
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class WalletDTO(
    val id: String?,
    @field:Min(1, message = "Balance must be >= 0")
    val balance: BigDecimal = BigDecimal(0),
    @field:NotNull(message = "UserID must not be null")
    val userID: String
)
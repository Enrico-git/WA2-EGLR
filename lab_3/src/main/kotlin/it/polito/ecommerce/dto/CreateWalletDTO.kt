package it.polito.ecommerce.dto

import javax.validation.constraints.Min

data class CreateWalletDTO (
    @field:Min(0)
    val id: Long
)
package it.polito.wa2.catalogservice.dto

import javax.validation.constraints.Min

data class CreateWalletDTO(
    @field:Min(0)
    val customerID: String
)
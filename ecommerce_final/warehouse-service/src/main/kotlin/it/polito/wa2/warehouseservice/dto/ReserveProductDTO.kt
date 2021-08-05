package it.polito.wa2.warehouseservice.dto

import java.math.BigDecimal

data class ReserveProductDTO(
        val id: String,
        val amount: Int,
        val price: BigDecimal? = null
)

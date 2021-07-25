package it.polito.wa2.orderservice.dto

import java.math.BigDecimal

data class ProductDTO (
    val id: String,
    val amount: Int,
    val price: BigDecimal? = null
)
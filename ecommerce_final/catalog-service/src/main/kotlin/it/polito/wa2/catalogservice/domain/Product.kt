package it.polito.wa2.catalogservice.domain

import java.math.BigDecimal

data class Product(
    val id: String?,
    val amount: Int,
    val price: BigDecimal
)

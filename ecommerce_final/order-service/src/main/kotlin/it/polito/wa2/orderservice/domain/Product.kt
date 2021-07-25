package it.polito.wa2.orderservice.domain

import it.polito.wa2.orderservice.dto.ProductDTO
import java.math.BigDecimal

data class Product(
    val id: String?,
    val amount: Int,
    val price: BigDecimal
)

fun Product.toDTO() = ProductDTO(
    id = id!!,
    amount = amount
)
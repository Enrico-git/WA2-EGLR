package it.polito.ecommerce.warehouse.dto

import it.polito.ecommerce.warehouse.domain.Product
import java.math.BigDecimal

data class ProductDTO(
    val id: Long?,
    val name: String,
    val category: String,
    val price: BigDecimal,
    val quantity: Long
)

fun Product.toDTO() = ProductDTO(
    id = id,
    name = name,
    category = category,
    price = price,
    quantity = quantity
)
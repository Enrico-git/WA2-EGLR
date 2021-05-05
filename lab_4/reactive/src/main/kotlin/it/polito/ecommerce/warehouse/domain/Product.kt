package it.polito.ecommerce.warehouse.domain

import org.springframework.data.annotation.Id
import java.math.BigDecimal


data class Product (
    @Id
    val id: Long?,
    val name: String,
    val category: String,
    var price: BigDecimal,
    var quantity: Long
)
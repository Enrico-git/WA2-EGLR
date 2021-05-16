package it.polito.ecommerce.warehouse.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table
data class Product(
    @Id
    val id: Long?,
    val name: String,
    val category: String,
    var price: BigDecimal,
    var quantity: Long,
    @Version
    val version: Long? = null
)
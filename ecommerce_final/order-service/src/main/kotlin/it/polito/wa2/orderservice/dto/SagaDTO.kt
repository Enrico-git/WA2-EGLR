package it.polito.wa2.orderservice.dto

import it.polito.wa2.orderservice.domain.ProductLocation
import java.math.BigDecimal

data class SagaDTO(
    val id: String,
    val type: String,
    val customerEmail: String,
    val amount: BigDecimal,
    val products: Set<ProductDTO>? = null,
    val productsWarehouseLocation: Set<ProductLocation>? = null,
    val auth: String
)
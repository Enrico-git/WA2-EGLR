package it.polito.wa2.orderservice.dto

import java.math.BigDecimal

data class SagaDTO(
    val id: String,
    val type: String,
    val customerEmail: String,
    val shippingAddress: String? = null,
    val amount: BigDecimal,
    val products: Set<ProductDTO>? = null,
    val auth: String
)
package it.polito.wa2.orderservice.dto

import java.sql.Timestamp

data class ProductsReservationRequestDTO(
    val orderID: String,
    val products: Set<ProductDTO>,
    val timestamp: Timestamp
)

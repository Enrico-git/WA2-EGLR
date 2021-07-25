package it.polito.wa2.orderservice.dto

import it.polito.wa2.orderservice.domain.ProductLocation

data class AbortProductReservationRequestDTO(
    val orderID: String,
    val productsWarehouseLocation: Set<ProductLocation>
)

package it.polito.wa2.orderservice.dto

import it.polito.wa2.orderservice.domain.ProductLocation

/**
 * {orderID: asdbasdb, productsWarehouseLocation: [{banane, wh1, 2}, {arance, wh2, 2}, {banane, wh3, 3}]
 */
data class ProductsReservationResponseDTO(
    val orderID: String,
    val productsWarehouseLocation: Set<ProductLocation>
)

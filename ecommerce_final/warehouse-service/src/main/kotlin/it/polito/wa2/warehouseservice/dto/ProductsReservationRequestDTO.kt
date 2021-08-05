package it.polito.wa2.warehouseservice.dto

import java.sql.Timestamp

data class ProductsReservationRequestDTO(
        val orderID: String,
        val products: Set<ReserveProductDTO>,
        val timestamp: Timestamp
)
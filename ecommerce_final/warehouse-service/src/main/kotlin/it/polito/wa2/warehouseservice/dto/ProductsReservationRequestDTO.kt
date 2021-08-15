package it.polito.wa2.warehouseservice.dto

import org.bson.types.ObjectId
import java.sql.Timestamp

data class ProductsReservationRequestDTO(
        val orderID: ObjectId,
        val products: Set<ReserveProductDTO>,
        val shippingAddress: String,
        val timestamp: Timestamp
)
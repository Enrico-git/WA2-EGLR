package it.polito.wa2.warehouseservice.dto

import org.bson.types.ObjectId
import java.sql.Timestamp

data class AbortProductReservationRequestDTO(
        val orderID: ObjectId,
        val timestamp: Timestamp
)

package it.polito.wa2.warehouseservice.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp


enum class DeliveryDescription {
        RESERVATION, CANCELLATION
}

@Document(collection = "deliveries")
data class Delivery (
        @Id
        val id: ObjectId?,
        val orderID: ObjectId,
        val timestamp: Timestamp,
        val products: MutableSet<ProductLocation>,
        val description: DeliveryDescription
)
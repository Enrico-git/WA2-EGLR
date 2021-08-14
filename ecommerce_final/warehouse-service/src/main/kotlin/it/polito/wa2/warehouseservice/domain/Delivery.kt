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
        val id: ObjectId,
        val orderId: ObjectId,
        val timestamp: Timestamp,
        val products: MutableSet<ProductLocation>,
        var status: DeliveryDescription
)
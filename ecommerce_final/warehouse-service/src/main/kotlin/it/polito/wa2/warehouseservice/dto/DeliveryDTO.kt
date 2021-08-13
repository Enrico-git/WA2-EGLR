package it.polito.wa2.warehouseservice.dto

import it.polito.wa2.warehouseservice.domain.Delivery
import it.polito.wa2.warehouseservice.domain.ProductLocation
import org.bson.types.ObjectId
import java.sql.Timestamp
import javax.validation.constraints.NotNull

data class DeliveryDTO(
        val id: String?,
        @field:NotNull(message = "order id must be not null")
        val orderId: String,
        @field:NotNull(message = "timestamp must be not null")
        val timestamp: Timestamp,
        val products: MutableSet<ProductLocation>
)

fun DeliveryDTO.toEntity() = Delivery(
        id = ObjectId(id),
        orderId = ObjectId(orderId),
        timestamp = timestamp,
        products = products
)
package it.polito.wa2.warehouseservice.dto

import it.polito.wa2.warehouseservice.domain.ProductLocation
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

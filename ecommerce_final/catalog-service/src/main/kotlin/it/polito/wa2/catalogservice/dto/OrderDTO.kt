package it.polito.wa2.catalogservice.dto

import it.polito.wa2.catalogservice.common.OrderStatus
import it.polito.wa2.catalogservice.constraintGroups.CreateOrder
import it.polito.wa2.catalogservice.constraintGroups.DeleteOrder
import it.polito.wa2.catalogservice.constraintGroups.UpdateOrder
import org.bson.types.ObjectId
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

data class OrderDTO(
    val id: ObjectId? = null,
    @field:NotNull(message = "Buyer must not be null", groups = [CreateOrder::class])
    val buyer: ObjectId? = null,
    @field:NotNull(message = "Products must not be null", groups = [CreateOrder::class])
    @field:Valid
    val products: Set<OrderProductDTO>? = null,
    @field:NotNull(message = "Delivery address must not be null", groups = [CreateOrder::class])
    val deliveryAddress: String? = null,
    @field:NotNull(message = "Order status must not be null", groups = [UpdateOrder::class])
    val status: OrderStatus? = null,
    @field:NotNull(message = "Customer email must not be null", groups = [UpdateOrder::class, DeleteOrder::class, CreateOrder::class])
    @field:Email(message = "Invalid email format", groups = [UpdateOrder::class, DeleteOrder::class, CreateOrder::class])
    val email: String? = null
)

package it.polito.wa2.orderservice.dto

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.constraintGroups.CreateOrder
import it.polito.wa2.orderservice.constraintGroups.DeleteOrder
import it.polito.wa2.orderservice.constraintGroups.UpdateOrder
import it.polito.wa2.orderservice.domain.Delivery
import it.polito.wa2.orderservice.domain.Product
import org.bson.types.ObjectId
import org.springframework.validation.annotation.Validated
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

data class OrderDTO(
    val id: ObjectId? = null,
    @field:NotNull(message = "Buyer must not be null", groups = [CreateOrder::class])
    val buyer: ObjectId? = null,
    @field:NotNull(message = "Products must not be null", groups = [CreateOrder::class])
    @field:Valid
    val products: Set<Product>? = null,
    @field:NotNull(message = "Delivery must not be null", groups = [CreateOrder::class])
    val delivery: Delivery? = null,
    @field:NotNull(message = "Order status must not be null", groups = [UpdateOrder::class])
    val status: OrderStatus? = null,
    @field:Email(message = "Invalid email format", groups = [UpdateOrder::class, DeleteOrder::class, CreateOrder::class])
    @field:NotNull(message = "Customer email must not be null", groups = [UpdateOrder::class, DeleteOrder::class, CreateOrder::class])
    val email: String? = null
)


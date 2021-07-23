package it.polito.wa2.catalogservice.domain

import it.polito.wa2.catalogservice.common.OrderStatus
import it.polito.wa2.catalogservice.dto.OrderDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "orders")
data class Order (
    @Id
    val id: ObjectId?,
    val buyer: ObjectId,
    val products: Set<Product>,
    var status: OrderStatus,
    val delivery: Delivery?,
    @Version
    val version: Long? = null
)

fun Order.toDTO() = OrderDTO(
    id = id!!,
    buyer = buyer,
    products = products,
    status = status,
    delivery = delivery
)
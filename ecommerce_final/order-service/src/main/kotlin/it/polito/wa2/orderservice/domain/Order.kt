package it.polito.wa2.orderservice.domain

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.dto.OrderDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "orders")
data class Order (
    @Id
    val id: ObjectId?,
    val buyer: String,
    val products: Set<Product>,
    var status: OrderStatus,
    @Version
    val version: Long? = null
)

fun Order.toDTO() = OrderDTO(
    id = id!!,
    buyer = buyer,
    products = products.map { it.toDTO() }.toHashSet(),
    status = status
)
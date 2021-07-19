package it.polito.wa2.orderservice.dto

import it.polito.wa2.orderservice.common.OrderStatus
import org.bson.types.ObjectId

data class OrderDTO(
    val id: ObjectId,
    val buyer: String,
    val products: Set<ProductDTO>,
    val status: OrderStatus
)


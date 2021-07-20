package it.polito.wa2.orderservice.dto

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.domain.Delivery
import org.bson.types.ObjectId

data class OrderDTO(
    val id: ObjectId?,
    val buyer: ObjectId?,
    val products: Set<ProductDTO>?,
    val delivery: Delivery?,
    val status: OrderStatus?
)


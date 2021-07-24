package it.polito.wa2.orderservice.dto

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.domain.Delivery
import it.polito.wa2.orderservice.domain.Product
import org.bson.types.ObjectId

data class OrderDTO(
    val id: ObjectId?,
    val buyer: ObjectId?,
    val products: Set<Product>?,
    val delivery: Delivery?,
    val status: OrderStatus?,
    val email: String? = null
)


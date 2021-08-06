package it.polito.wa2.catalogservice.dto

import it.polito.wa2.catalogservice.common.OrderStatus
import it.polito.wa2.catalogservice.domain.Delivery
import it.polito.wa2.catalogservice.domain.Product
import org.bson.types.ObjectId

data class OrderDTO(
    val id: ObjectId? = null,
    val buyer: ObjectId? = null,
    val products: Set<Product>? = null,
    val delivery: Delivery? = null,
    val status: OrderStatus? = null,
    val email: String? = null
)
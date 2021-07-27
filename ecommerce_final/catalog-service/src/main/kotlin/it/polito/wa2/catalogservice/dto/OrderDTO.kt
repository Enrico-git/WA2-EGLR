package it.polito.wa2.catalogservice.dto

import it.polito.wa2.catalogservice.common.OrderStatus
import it.polito.wa2.catalogservice.domain.Delivery
import it.polito.wa2.catalogservice.domain.Product
import org.bson.types.ObjectId

data class OrderDTO(
    val id: ObjectId?,
    val buyer: ObjectId?,
    val products: Set<Product>?,
    val delivery: Delivery?,
    val status: OrderStatus?,
    val email: String? = null
)
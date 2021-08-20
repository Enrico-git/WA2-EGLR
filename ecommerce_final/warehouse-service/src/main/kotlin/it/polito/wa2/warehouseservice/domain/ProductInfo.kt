package it.polito.wa2.warehouseservice.domain

import org.bson.types.ObjectId

data class ProductInfo(
    val productId: ObjectId, //ProductId
    var alarm: Int,
    var quantity: Int
)


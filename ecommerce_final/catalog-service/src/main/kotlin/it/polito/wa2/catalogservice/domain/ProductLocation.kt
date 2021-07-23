package it.polito.wa2.catalogservice.domain

import org.bson.types.ObjectId

//[{banane, wh1, 2}, {arance, wh2, 2}, {banane, wh3, 3}]
data class ProductLocation(
    val productID: ObjectId,
    val warehouseID: ObjectId,
    val amount: Long
)
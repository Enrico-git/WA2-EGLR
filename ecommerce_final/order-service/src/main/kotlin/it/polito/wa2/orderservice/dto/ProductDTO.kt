package it.polito.wa2.orderservice.dto

import org.bson.types.ObjectId
import java.math.BigDecimal

data class ProductDTO (
    val id: ObjectId,
    val amount: Int,
    val price: BigDecimal
)
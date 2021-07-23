package it.polito.wa2.catalogservice.dto

import org.bson.types.ObjectId
import java.math.BigDecimal

data class ProductDTO (
    val id: ObjectId,
    val amount: Int,
    val price: BigDecimal
)
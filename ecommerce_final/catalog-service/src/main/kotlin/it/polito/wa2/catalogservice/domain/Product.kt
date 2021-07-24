package it.polito.wa2.catalogservice.domain

import it.polito.wa2.catalogservice.dto.ProductDTO
import org.bson.types.ObjectId
import java.math.BigDecimal

class Product(
    val id: ObjectId?,
    val amount: Int,
    val price: BigDecimal
)

fun Product.toDTO() = ProductDTO(
    id = id!!,
    amount = amount,
    price = price
)
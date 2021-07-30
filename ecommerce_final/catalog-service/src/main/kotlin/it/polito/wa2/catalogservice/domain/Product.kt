package it.polito.wa2.catalogservice.domain

import it.polito.wa2.catalogservice.dto.ProductDTO
import org.bson.types.ObjectId
import java.math.BigDecimal

data class Product(
    val id: String?,
    val amount: Int,
    val price: BigDecimal
)

//fun Product.toDTO() = ProductDTO(
//    id = id!!,
//    amount = amount
//)
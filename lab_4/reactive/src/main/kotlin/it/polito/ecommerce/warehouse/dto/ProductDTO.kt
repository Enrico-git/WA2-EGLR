package it.polito.ecommerce.warehouse.dto

import it.polito.ecommerce.warehouse.domain.Product
import java.math.BigDecimal

data class ProductDTO (
    val id: Long?,
    val name: String?,
    val category: String?,
    val price: BigDecimal?,
    val quantity: Long
){
//    fun isValid(): Boolean{
//        return id ==null || (id != null && id > 0)
//    }
}

fun Product.toDTO(): ProductDTO {
    return ProductDTO(
        id = id,
        name = name,
        category = category,
        price = price,
        quantity = quantity
    )
}
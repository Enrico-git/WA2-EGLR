package it.polito.ecommerce.warehouse.dto

import it.polito.ecommerce.warehouse.domain.Product
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.validation.constraints.Positive

data class ProductDTO(
    val id: Long?,
//    @field:Length(min = 2)
    val name: String,
//    @field:Length(min = 2)
    val category: String,
//    @field:Positive
    val price: BigDecimal,
    val quantity: Long
)

fun Product.toDTO() = ProductDTO(
    id = id,
    name = name,
    category = category,
    price = price,
    quantity = quantity
)
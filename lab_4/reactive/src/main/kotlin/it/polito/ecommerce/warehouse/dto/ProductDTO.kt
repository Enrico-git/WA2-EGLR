package it.polito.ecommerce.warehouse.dto

import it.polito.ecommerce.warehouse.domain.Product
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal
import javax.validation.constraints.Positive

data class ProductDTO(
    val id: Long?,
    val name: String?,
    val category: String?,
    val price: BigDecimal?,
    val quantity: Long
) {
    fun isValid(): Boolean {
        return name!!.length >= 2 &&
                category!!.length >= 2 &&
                price!! > BigDecimal.ZERO &&
                quantity >= 0
    }
}

fun Product.toDTO() = ProductDTO(
    id = id,
    name = name,
    category = category,
    price = price,
    quantity = quantity
)
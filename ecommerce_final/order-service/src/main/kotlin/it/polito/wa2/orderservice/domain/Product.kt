package it.polito.wa2.orderservice.domain

import it.polito.wa2.orderservice.constraintGroups.CreateOrder
import it.polito.wa2.orderservice.dto.ProductDTO
import java.math.BigDecimal
import javax.validation.constraints.Min

data class Product(
    val id: String?,
    @field:Min(1, message = "Amount must be > 0", groups = [CreateOrder::class])
    val amount: Int,
    val price: BigDecimal
)

fun Product.toDTO() = ProductDTO(
    id = id!!,
    amount = amount
)
package it.polito.wa2.catalogservice.domain

import it.polito.wa2.catalogservice.constraintGroups.CreateOrder
import java.math.BigDecimal
import javax.validation.constraints.Min

data class Product(
    val id: String?,
    @field:Min(1, message = "Amount must be > 0", groups = [CreateOrder::class])
    val amount: Int,
    @field:Min(0, message = "Price must be > 0", groups = [CreateOrder::class])
    val price: BigDecimal
)

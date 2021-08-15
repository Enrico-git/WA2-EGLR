package it.polito.wa2.catalogservice.dto

import it.polito.wa2.catalogservice.constraintGroups.CreateOrder
import java.math.BigDecimal
import javax.validation.constraints.Min
import javax.validation.constraints.Null

data class OrderProductDTO(
    val id: String?,
    @field:Min(1, message = "Amount must be > 0", groups = [CreateOrder::class])
    val amount: Int,
    @field:Null(message = "Price must be null", groups = [CreateOrder::class])
    var price: BigDecimal?
)

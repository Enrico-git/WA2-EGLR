package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Customer
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CustomerDTO(
    @field:Min(0, message = "the customer ID must be higher than 0")
    @field:NotNull(message = "the id must not be null")
    val id: Long?,
    val name: String?,
    val surname: String?
)

fun Customer.toDTO() = CustomerDTO(
    id = getId()!!,
    name = name,
    surname = surname
)
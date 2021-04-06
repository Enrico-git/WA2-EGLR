package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Customer
import javax.validation.constraints.Min

data class CustomerDTO(
    @field:Min(0)
    val id: Long,
    val name: String?,
    val surname: String?
)

fun Customer.toDTO() = CustomerDTO(
    id = id!!,
    name = name,
    surname = surname
)
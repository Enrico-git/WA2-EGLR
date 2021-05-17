package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Customer
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

class CustomerDTO(
    @field:Min(0)
    @field:NotNull
    val id: Long?,
    val name: String?,
    val surname: String?
) {
}

fun Customer.toDTO(): CustomerDTO {
    return CustomerDTO(
        id = getId()!!,
        name = name,
        surname = surname
    )
}

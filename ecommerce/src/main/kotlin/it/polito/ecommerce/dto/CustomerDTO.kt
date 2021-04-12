package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Customer
import javax.validation.constraints.Min

data class CustomerDTO(
    @field:Min(0, message ="the customer ID must be higher than zero")
    val id:Long,
    val name: String?,
    val surname: String?)

fun Customer.toDTO() = CustomerDTO(
    id = getId()!!,
    name = name,
    surname = surname)
package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Customer

data class CustomerDTO(
    val id: Int,
    val name: String,
    val surname: String
)

fun Customer.toDTO() = CustomerDTO(
    id = id!!,
    name = name,
    surname = surname
)
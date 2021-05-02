package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Customer
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CustomerDTO(
    @field:Min(0)
    val id: Long?,
    @field:Length(min = 2, max = 50)
    val name: String,
    @field:Length(min = 2, max = 50)
    val surname: String,
    val address: String,
    @field:Email
    val email: String,
    @field:Min(0)
    val userID: Long
)

fun Customer.toDTO() = CustomerDTO(
    id = getId()!!,
    name = name,
    surname = surname,
    address = address,
    email = email,
    userID = user.getId()!!
)
package it.polito.wa2.catalogservice.dto

import it.polito.wa2.catalogservice.domain.Customer
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email

data class CustomerDTO(
    val id: ObjectId?,
    @field:Length(min = 2, max = 50)
    val name: String,
    @field:Length(min = 2, max = 50)
    val surname: String,
    val address: String,
    @field:Email
    val email: String,
    val userID: ObjectId
)

fun Customer.toDTO() = CustomerDTO(
    id = id,
    name = name,
    surname = surname,
    address = address,
    email = email,
    userID = user
)
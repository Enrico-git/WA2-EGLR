package it.polito.wa2.catalogservice.dto

import it.polito.wa2.catalogservice.domain.Customer
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.Min

data class CustomerDTO(
    @field:Min(0)
    val id: ObjectId?,
    @field:Length(min = 2, max = 50)
    val name: String,
    @field:Length(min = 2, max = 50)
    val surname: String,
    val address: String,
    @field:Email
    val email: String,
    @field:Min(0)
    val userID: ObjectId
)

fun Customer.toDTO() = CustomerDTO(
    id = id,
    name = name,
    surname = surname,
    address = address,
    email = email,
    userID = user.id!!
)
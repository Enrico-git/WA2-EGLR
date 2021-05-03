package it.polito.ecommerce.dto

import it.polito.ecommerce.domain.Customer
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.Min

data class CustomerRegistrationDTO(
    val id: Long?,

    @field:Length(min = 2, max = 50)
    val name: String,

    @field:Length(min = 2, max = 50)
    val surname: String,

    @field:Length(min = 2, max = 50)
    val address: String,

    @field:Length(min = 2, max = 50)
    @field:Email
    val email: String,

    @field:Min(1)
    val userID: Long
)
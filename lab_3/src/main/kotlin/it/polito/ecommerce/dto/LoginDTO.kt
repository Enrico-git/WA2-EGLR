package it.polito.ecommerce.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Pattern

data class LoginDTO(
    var token: String?,

    @field:Length(min = 5, max = 50)
    val username: String,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @field:Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,50}$", message="You must use at least 1 capital letter and one digitjavax.validation.constraints.Patter.message=You must use at least 1 capital letter and one digit") //With whitespace; without  special char
    var password: String? = null,


    var roles: MutableSet<String>? = null
) {
}
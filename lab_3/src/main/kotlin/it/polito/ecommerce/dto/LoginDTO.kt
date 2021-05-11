package it.polito.ecommerce.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Pattern

data class LoginDTO(
    @field:Length(min = 5, max = 50)
    val username: String,
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,50}\$",
        message = "you must use at least one capital letter and one digit"
    ) //WITH WHITE SPACES
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String?,
    var token: String? = null,
    var roles: MutableSet<String>? = null
)
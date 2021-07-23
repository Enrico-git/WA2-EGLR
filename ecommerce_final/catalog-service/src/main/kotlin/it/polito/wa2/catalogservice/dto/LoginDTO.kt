package it.polito.wa2.catalogservice.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Pattern

data class LoginDTO(
    var jwt: String?,
    @field:Length(min = 5, max = 50)
    val username: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,50}\$",
        message = "you must use at least one capital letter and one digit"
    ) //WITH WHITE SPACES
    var password: String?,
    var roles: MutableSet<String>?
) {
}
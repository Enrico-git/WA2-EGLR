package it.polito.ecommerce.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Pattern

data class LoginDTO(
//    @field:Length(min = 5)
    val username: String,
//    @field:Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,50}\$", message="you must use at least one capital letter and one digit") //WITH WHITE SPACES
    var password: String?,
    var token: String? = null,
    var roles: MutableSet<String>? = null
) {
    fun clearSensibleData(): LoginDTO{
        password = null
        return this
    }
}
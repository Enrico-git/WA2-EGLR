package it.polito.ecommerce.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Length

data class LoginDTO(
    var token: String?,
    //TODO add validation to DTOs
    //@field:Length(min = 5, max = 15)
    val username: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var password: String? = null,
    var roles: MutableSet<String>? = null
) {

    fun clearSensibleData(): LoginDTO {
        password = null
        return this
    }
}
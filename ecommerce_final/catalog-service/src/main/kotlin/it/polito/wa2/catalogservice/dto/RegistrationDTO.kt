package it.polito.wa2.catalogservice.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

data class RegistrationDTO(
    @field:Length(min = 5, max = 50)
    val username: String,
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,50}\$",
        message = "you must use at least one capital letter and one digit"
    ) //WITH WHITE SPACES
    val password: String,
    val confirmPassword: String,
    @field:Email
    val email: String,
) {
    @AssertTrue(message = "the passwords do not match")
    fun isValid(): Boolean {
        return this.password == this.confirmPassword
    }
}
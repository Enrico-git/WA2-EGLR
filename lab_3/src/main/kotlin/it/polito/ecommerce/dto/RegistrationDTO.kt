package it.polito.ecommerce.dto


import org.hibernate.validator.constraints.Length
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern

data class RegistrationDTO (
    @field:Length(min = 5, max = 15)
    val username: String,

    @field:Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,50}$", message="You must use at least 1 capital letter and one digitjavax.validation.constraints.Patter.message=You must use at least 1 capital letter and one digit") //With whitespace; without  special char
    val password: String,

    @field:Email
    val email: String,

    val confirmPassword: String? = null
    ) {
    @AssertTrue(message="The passwords do not match")
    fun isValid():Boolean {
        return this.password == this.confirmPassword
    }
}
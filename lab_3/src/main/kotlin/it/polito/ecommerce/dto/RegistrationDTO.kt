package it.polito.ecommerce.dto

import it.polito.ecommerce.common.Rolename
import org.hibernate.validator.constraints.Length
import javax.persistence.FieldResult
import javax.validation.constraints.AssertTrue
import javax.validation.constraints.Email
import javax.validation.constraints.Pattern


data class RegistrationDTO(
    @field:Length(min = 5, max = 15)
    val username: String,
    @field:Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[A-Z]).{8,50}\$", message="You must use at least 1 capital letter and one digit") //WITH WHITESPACES ;D
    val password: String,
    val confirmPassword: String,
    @field:Email
    val email: String,
){
    @AssertTrue(message="The passwords do not match")
    fun isValid():Boolean {
        return this.password == this.confirmPassword
    }
}
package it.polito.wa2.catalogservice.queries

import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.dto.RegistrationDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.services.UserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus

@Component
class UserQuery(private val userService: UserDetailsService) {

    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUser(registrationDTO: RegistrationDTO): UserDetailsDTO {
        return userService.registerUser(registrationDTO)
    }

    @ResponseStatus(HttpStatus.OK)
    suspend fun signIn(loginDTO: LoginDTO): LoginDTO {
        return userService.authAndCreateToken(loginDTO)
    }

    @ResponseStatus(HttpStatus.OK)
    suspend fun registrationConfirm(token: String) {
        return userService.verifyToken(token)
    }
}
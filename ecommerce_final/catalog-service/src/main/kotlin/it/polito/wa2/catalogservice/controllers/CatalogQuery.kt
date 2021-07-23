package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.services.UserDetailsService
import org.springframework.stereotype.Component
import com.expediagroup.graphql.spring.operations.Query
import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.dto.RegistrationDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller

@Component
//@Controller TODO try if graphql needs the Annotation @Controller
class CatalogQuery(private val userService: UserDetailsService): Query {

    suspend fun createUser(registrationDTO: RegistrationDTO): ResponseEntity<UserDetailsDTO> {
        return ResponseEntity(userService.registerUser(registrationDTO), HttpStatus.CREATED)
    }

    suspend fun signIn(loginDTO: LoginDTO): ResponseEntity<LoginDTO> {
        return ResponseEntity(userService.authAndCreateToken(loginDTO), HttpStatus.OK)
    }

    suspend fun registrationConfirm(token: String): ResponseEntity<Unit> {
        return ResponseEntity(userService.verifyToken(token), HttpStatus.OK)
    }
}
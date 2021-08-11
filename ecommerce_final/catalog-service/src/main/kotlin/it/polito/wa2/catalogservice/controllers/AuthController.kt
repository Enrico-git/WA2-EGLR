package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.dto.RegistrationDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.services.UserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserDetailsService
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUser(@RequestBody @Validated registrationDTO: RegistrationDTO): UserDetailsDTO {
        return userService.registerUser(registrationDTO)
    }

    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    suspend fun signIn(@RequestBody @Validated loginDTO: LoginDTO): LoginDTO {
        return userService.authAndCreateToken(loginDTO)
    }

    @GetMapping("/registrationConfirm")
    @ResponseStatus(HttpStatus.OK)
    suspend fun registrationConfirm(@RequestParam token: String) {
        return userService.verifyToken(token)
    }
}
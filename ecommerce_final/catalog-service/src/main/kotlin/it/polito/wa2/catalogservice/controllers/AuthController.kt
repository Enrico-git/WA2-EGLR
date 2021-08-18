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
    /**
     * API endpoint to register an user
     * @param registrationDTO the details of the order
     * @return the created user details
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUser(@RequestBody @Validated registrationDTO: RegistrationDTO): UserDetailsDTO {
        return userService.registerUser(registrationDTO)
    }

    /**
     * API endpoint to do the login
     * @param loginDTO the DTO with the details to log in
     * @return the LoginDTO object
     */
    @PostMapping("/signin")
    @ResponseStatus(HttpStatus.OK)
    suspend fun signIn(@RequestBody @Validated loginDTO: LoginDTO): LoginDTO {
        return userService.authAndCreateToken(loginDTO)
    }

    /**
     * API endpoint to confirm the registration clicking the link in the email received during
     * the registration (link with the token to verify)
     * @param token the registration token
     * @return
     */
    @GetMapping("/registrationConfirm")
    @ResponseStatus(HttpStatus.OK)
    suspend fun registrationConfirm(@RequestParam token: String) {
        return userService.verifyToken(token)
    }
}
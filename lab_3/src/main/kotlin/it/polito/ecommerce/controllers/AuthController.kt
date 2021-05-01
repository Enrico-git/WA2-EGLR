package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.LoginDTO
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.security.JwtUtils
import it.polito.ecommerce.services.UserDetailsServiceExt
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val userService: UserDetailsServiceExt
) {
    @PostMapping("/register")
    fun register(@RequestBody @Valid registrationDTO: RegistrationDTO): ResponseEntity<UserDetailsDTO>{
        return ResponseEntity(userService.registerUser(registrationDTO), HttpStatus.CREATED)
    }

    @GetMapping("/registrationConfirm")
    fun confirmRegistration(@RequestParam token: String): ResponseEntity<Unit>{
        return ResponseEntity(userService.verifyToken(token), HttpStatus.OK)
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody @Valid loginDTO: LoginDTO): ResponseEntity<LoginDTO>{
        return ResponseEntity(userService.authAndCreateToken(loginDTO), HttpStatus.OK)
    }

}
package it.polito.ecommerce.controllers

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
    private val userService: UserDetailsServiceExt,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils,
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
    fun signIn(@RequestBody @Valid userDetailsDTO: UserDetailsDTO): ResponseEntity<Any>{
        println("ausduasd")
        println(userDetailsDTO)
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(userDetailsDTO.username, userDetailsDTO.password))
        SecurityContextHolder.getContext().authentication = authentication

        println(SecurityContextHolder.getContext().authentication)

        return ResponseEntity(jwtUtils.generateJwtToken(authentication), HttpStatus.OK)
    }


}
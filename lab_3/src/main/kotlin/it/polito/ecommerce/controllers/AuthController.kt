package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val userService: UserDetailsServiceImpl
) {
    @PostMapping("/register")
    fun register(@RequestBody @Valid registrationDTO: RegistrationDTO): ResponseEntity<UserDetailsDTO>{
        return ResponseEntity(userService.registerUser(registrationDTO), HttpStatus.CREATED)
    }

//    @PostMapping("/signin")
//    fun logIn(@RequestBody @Valid userDetailsDTO: UserDetailsDTO): ResponseEntity<UserDetailsDTO>{
//        return ResponseEntity(userService.addUser(userDetailsDTO), HttpStatus.CREATED)
//    }


}
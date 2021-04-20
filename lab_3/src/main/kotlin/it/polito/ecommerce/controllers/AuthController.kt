package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.services.UserDetailsServiceExt
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val userService: UserDetailsServiceExt
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid registrationDTO: RegistrationDTO) : ResponseEntity<UserDetailsDTO>{
        return ResponseEntity(userService.registerUser(registrationDTO) , HttpStatus.OK)
    }
}
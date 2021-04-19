package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.services.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
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
    private val userService: UserDetailsServiceImpl
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid userDetailsDTO: UserDetailsDTO) : ResponseEntity<UserDetailsDTO>{
        return ResponseEntity(userService.addUser(userDetailsDTO) , HttpStatus.OK)
    }
}
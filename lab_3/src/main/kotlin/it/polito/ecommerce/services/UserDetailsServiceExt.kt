package it.polito.ecommerce.services

import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import org.springframework.security.core.userdetails.UserDetailsService


interface UserDetailsServiceExt : UserDetailsService {

    fun registerUser(registrationDTO: RegistrationDTO): UserDetailsDTO
    fun addRole(username: String, role: String): UserDetailsDTO
    fun removeRole(username: String, role: String): UserDetailsDTO
    fun enableUser(username: String): UserDetailsDTO
    fun disableUser(username: String): UserDetailsDTO

    }
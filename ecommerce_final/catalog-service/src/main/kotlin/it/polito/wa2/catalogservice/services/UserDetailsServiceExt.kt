package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.dto.RegistrationDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UserDetails


interface UserDetailsService {
    suspend fun findByUsername(username: String): UserDetails
    suspend fun registerUser(registrationDTO: RegistrationDTO): UserDetailsDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addRole(username: String, role: String): UserDetailsDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun removeRole(username: String, role: String): UserDetailsDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun enableUser(username: String): UserDetailsDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun disableUser(username: String): UserDetailsDTO
    suspend fun verifyToken(token: String)
    suspend fun authAndCreateToken(loginDTO: LoginDTO): LoginDTO
}
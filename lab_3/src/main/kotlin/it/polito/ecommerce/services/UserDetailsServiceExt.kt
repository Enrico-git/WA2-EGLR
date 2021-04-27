package it.polito.ecommerce.services

import it.polito.ecommerce.dto.LoginDTO
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService

interface UserDetailsServiceExt : UserDetailsService {

    fun registerUser(registrationDTO: RegistrationDTO): UserDetailsDTO

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun addRole(username: String, role: String): UserDetailsDTO

    //TODO understand why hasRole not work
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun removeRole(username: String, role:String): UserDetailsDTO

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun enableUser(username: String) : UserDetailsDTO

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun disableUser(username: String) : UserDetailsDTO

    fun verifyToken(token: String)
    fun authAndCreateToken(loginDTO: LoginDTO, authenticationManager: AuthenticationManager): LoginDTO
}
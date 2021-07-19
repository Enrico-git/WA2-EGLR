package it.polito.wa2.orderservice.services


import it.polito.wa2.orderservice.dto.toDTO
import it.polito.wa2.orderservice.repositories.users.UserRepository
import it.polito.wa2.orderservice.security.JwtUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserDetailsServiceExtImpl(
    private val userRepository: UserRepository,
//    private val notificationService: NotificationServiceImpl,
    @Lazy private val authenticationManager: AuthenticationManager,
//    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
//    private val mailService: MailServiceImpl
) : UserDetailsService {

//    @Value("\${application.serverURL}")
//    private val serverURL = ""

    override suspend fun findByUsername(username: String): UserDetails {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw UsernameNotFoundException("User not found")
        return userOpt.get().toDTO()
    }

}
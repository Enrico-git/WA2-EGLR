package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.common.Rolename
import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.dto.RegistrationDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.repositories.UserRepository
import it.polito.wa2.catalogservice.security.JwtUtils
import it.polito.wa2.catalogservice.dto.toDTO
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import kotlinx.coroutines.reactive.asFlow
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
import java.lang.IllegalArgumentException
import java.sql.Timestamp

@Service
@Transactional
class UserDetailsServiceExtImpl(
    private val userRepository: UserRepository,
    private val verificationRepository: EmailVerificationTokenRepository,
    private val notificationService: NotificationServiceImpl,
    @Lazy private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
    private val mailService: MailServiceImpl
) : UserDetailsService {

    @Value("\${application.serverURL}")
    private val serverURL = ""

    override suspend fun findByUsername(username: String): UserDetails {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw UsernameNotFoundException("User not found")
        return userOpt.get().toDTO()
    }

    override suspend fun registerUser(registrationDTO: RegistrationDTO): UserDetailsDTO {
        var user = User(
            username = registrationDTO.username,
            password = passwordEncoder.encode(registrationDTO.password),
            isEnabled = false,
            email = registrationDTO.email,
            roles = Rolename.CUSTOMER.toString()
        )

        user = userRepository.save(user)
        val token = notificationService.createEmailVerificationToken(user)
        mailService.sendMessage(
            registrationDTO.email, "Confirm registration",
            "<a href='http://$serverURL/registrationConfirm?$token'>Click here</a>"
        )
        return user.toDTO()
    }

    override suspend fun addRole(username: String, role: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.addRole(Rolename.valueOf(role))

        return userRepository.save(user).toDTO()
    }

    override suspend fun removeRole(username: String, role: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.removeRole(Rolename.valueOf(role))

        return userRepository.save(user).toDTO()

    }

    override suspend fun enableUser(username: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.isEnabled = true

        return userRepository.save(user).toDTO()

    }

    override suspend fun disableUser(username: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.isEnabled = false

        return userRepository.save(user).toDTO()
    }

    override suspend fun verifyToken(token: String): Unit {
        val emailVerificationOpt = verificationRepository.findByToken(token)
        if (!emailVerificationOpt.isPresent)
            throw IllegalArgumentException("The token does not exist")
        if (emailVerificationOpt.get().expiryDate <= Timestamp(System.currentTimeMillis()))
            throw IllegalArgumentException("Token expired")
        enableUser(emailVerificationOpt.get().user.username)
    }

    override fun authAndCreateToken(loginDTO: LoginDTO): LoginDTO {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginDTO.username, loginDTO.password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        loginDTO.jwt = jwtUtils.generateJwtToken(authentication)
        loginDTO.roles =
            SecurityContextHolder.getContext().authentication.authorities.map { it.authority }.toMutableSet()
        return loginDTO
    }

}
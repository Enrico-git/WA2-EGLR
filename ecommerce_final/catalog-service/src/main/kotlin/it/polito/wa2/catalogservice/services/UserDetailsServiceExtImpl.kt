package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.common.Rolename
import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.dto.RegistrationDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.dto.toDTO
import it.polito.wa2.catalogservice.exceptions.UnauthorizedException
import it.polito.wa2.catalogservice.repositories.EmailVerificationTokenRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import it.polito.wa2.catalogservice.security.AuthenticationManager
import it.polito.wa2.catalogservice.security.JwtUtils
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.http.auth.UsernamePasswordCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        return userRepository.findByUsername(username)?.toDTO() ?: throw UsernameNotFoundException("User not found")
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
            "<a href='http://$serverURL/auth/registrationConfirm?token=$token'>Click here</a>"
        )
        return user.toDTO()
    }

    override suspend fun addRole(username: String, role: String): UserDetailsDTO {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("The user does not exist")
        user.addRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()
    }

    override suspend fun removeRole(username: String, role: String): UserDetailsDTO {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("The user does not exist")
        user.removeRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()

    }

    override suspend fun enableUser(username: String): UserDetailsDTO {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("The user does not exist")
        user.isEnabled = true
        return userRepository.save(user).toDTO()

    }

    override suspend fun disableUser(username: String): UserDetailsDTO {
        val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("The user does not exist")
        user.isEnabled = false
        return userRepository.save(user).toDTO()
    }

    override suspend fun verifyToken(token: String) {
        val emailVerification = verificationRepository.findByToken(token) ?: throw IllegalArgumentException("The token does not exist")
        if (emailVerification.expiryDate <= Timestamp(System.currentTimeMillis()))
            throw IllegalArgumentException("Token expired")
        enableUser(emailVerification.user.username)

    }

    override suspend fun authAndCreateToken(loginDTO: LoginDTO): LoginDTO {
        val user = findByUsername(loginDTO.username) as UserDetailsDTO
        if ( passwordEncoder.matches(loginDTO.password, user.password )) {
            loginDTO.jwt =
                jwtUtils.generateJwtToken(UsernamePasswordAuthenticationToken(user, loginDTO.password))
            loginDTO.roles = user.roles?.split(",")?.toMutableSet()
            return loginDTO
        } else {
            throw UnauthorizedException("Invalid Login")
        }
    }
}
package it.polito.ecommerce.services

import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.dto.LoginDTO
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.EmailVerificationTokenRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.security.JwtUtils
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
import java.lang.IllegalArgumentException
import java.sql.Timestamp
import javax.transaction.Transactional

@Service
@Transactional
class UserDetailsServiceExtImpl(
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val mailService: MailService,
    private val verificationRepository: EmailVerificationTokenRepository,
    @Lazy private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsServiceExt {
    @Value("\${application.serverURL}")
    val serverURL = ""

    override fun loadUserByUsername(username: String): UserDetails {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw UsernameNotFoundException("User not found")
        return userOpt.get().toDTO()
    }

    override fun registerUser(registrationDTO: RegistrationDTO): UserDetailsDTO {
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

    override fun addRole(username: String, role: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.addRole(Rolename.valueOf(role))

        return userRepository.save(user).toDTO()

    }

    override fun removeRole(username: String, role: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.removeRole(Rolename.valueOf(role))

        return userRepository.save(user).toDTO()

    }

    override fun enableUser(username: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.isEnabled = true

        return userRepository.save(user).toDTO()

    }

    override fun disableUser(username: String): UserDetailsDTO {
        val userOpt = userRepository.findByUsername(username)
        if (!userOpt.isPresent)
            throw IllegalArgumentException("The user does not exist")

        val user = userOpt.get()
        user.isEnabled = false

        return userRepository.save(user).toDTO()
    }

    override fun verifyToken(token: String): Unit {
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

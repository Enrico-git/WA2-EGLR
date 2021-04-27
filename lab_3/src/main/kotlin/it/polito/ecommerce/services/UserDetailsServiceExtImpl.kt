package it.polito.ecommerce.services

import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.EmailVerificationTokenRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.security.JwtUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
@Transactional
class UserDetailsServiceExtImpl(
    private val userRepository: UserRepository,
    private val notificationService: NotificationServiceImpl,
    private val verificationRepository: EmailVerificationTokenRepository,
    private val mailService: MailServiceImpl
): UserDetailsServiceExt {

    @Value("\${serverURL}")
    private val serverURL = ""

    override fun loadUserByUsername(username: String): UserDetails {
        val userOpt = userRepository.findByUsername(username)
        if ( ! userOpt.isPresent)
            throw UsernameNotFoundException("User not found")
        return userOpt.get().toDTO()
    }

    override fun registerUser(registrationDTO: RegistrationDTO): UserDetailsDTO{
//        TODO hash pw

        var user = User(
            username = registrationDTO.username,
            password = registrationDTO.password,
            isEnabled = false,
            email = registrationDTO.email,
            roles = Rolename.CUSTOMER.toString()
        )

        user = userRepository.save(user)
        val token = notificationService.createEmailVerificationToken(user)
        mailService.sendMessage(registrationDTO.email, "Confirm registration", "Click here: $serverURL/auth/registerConfirmation?token=$token")
//        username, email, roles, isEnabled
//        TODO RETURN TYPE

        return user.toDTO().clearSensibleData()
    }

    override fun addRole(username: String, role: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.addRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()
    }

    override fun removeRole(username: String, role: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.removeRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()
    }

    override fun enableUser(username: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.isEnabled = true
        return userRepository.save(user).toDTO()
    }

    override fun disableUser(username: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.isEnabled = false
        return userRepository.save(user).toDTO()
    }

    override fun verifyToken(token: String) {
        val emailVerificationOpt = verificationRepository.findByToken(token)
        if (! emailVerificationOpt.isPresent )
            throw IllegalArgumentException("The token does not exist")

        val emailVerificationToken = emailVerificationOpt.get()
        if (Timestamp(System.currentTimeMillis()) >= emailVerificationToken.expiryDate)
            throw IllegalArgumentException("The token is expired")

//        TODO change enableUser to user User directly and not username
        enableUser(emailVerificationToken.user.username)

    }
}
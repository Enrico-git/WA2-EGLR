package it.polito.ecommerce.services

import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import javax.transaction.Transactional

@Service
@Transactional
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val mailService: MailService
) : UserDetailsServiceExt {

    override fun loadUserByUsername(username: String?): UserDetails {
        val userOpt = userRepository.findByUsername(username!!)
        if ( ! userOpt.isPresent)
            throw UsernameNotFoundException("User not found")
        return userOpt.get().toDTO()
    }

    override fun registerUser(registrationDTO: RegistrationDTO): UserDetailsDTO{
        var user = User(
            username = registrationDTO.username,
            password = registrationDTO.password,
            email = registrationDTO.email,
            roles = Rolename.CUSTOMER.toString()
        )

        user = userRepository.save(user) //TODO do we save user before the confermation

        val token = notificationService.createEmailVerificationToken(user)
        mailService.sendMessage(registrationDTO.email, "Confirm registration", "Token: $token")
        return user.toDTO()
    }

    override fun addRole(username: String, role: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if(! userOpt.isPresent){
            throw IllegalArgumentException("the user does not exist")
        }

        val user = userOpt.get()
        user.addRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()
    }

    override fun removeRole(username: String, role: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if(! userOpt.isPresent){
            throw IllegalArgumentException("the user does not exist")
        }

        val user = userOpt.get()
        user.removeRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()
    }

    override fun enableUser(username: String) : UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if(! userOpt.isPresent){
            throw IllegalArgumentException("the user does not exist")
        }

        val user = userOpt.get()
        user.isEnabled = true
        return userRepository.save(user).toDTO()
    }

    override fun disableUser(username: String) : UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if(! userOpt.isPresent){
            throw IllegalArgumentException("the user does not exist")
        }

        val user = userOpt.get()
        user.isEnabled = false
        return userRepository.save(user).toDTO()
    }
}
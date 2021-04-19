package it.polito.ecommerce.services

import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.UserRepository
import javassist.NotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        val userOpt = userRepository.findByUsername(username!!)
        if ( ! userOpt.isPresent)
            throw UsernameNotFoundException("Wallet not found")
        return userOpt.get().toDTO()
    }

    fun addUser(userDetailsDTO: UserDetailsDTO): UserDetailsDTO{
//        TODO hash pw
        val user = User(
            username = userDetailsDTO.username,
            password = userDetailsDTO.password,
            isEnabled = false,
            email = userDetailsDTO.email,
            roles = Rolename.CUSTOMER.toString()
        )

        return userRepository.save(user).toDTO()
    }

    fun addRole(username: String, role: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.addRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()
    }

    fun removeRole(username: String, role: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.removeRole(Rolename.valueOf(role))
        return userRepository.save(user).toDTO()
    }
    fun enableUser(username: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.isEnabled = true
        return userRepository.save(user).toDTO()
    }

    fun disableUser(username: String): UserDetailsDTO{
        val userOpt = userRepository.findByUsername(username)
        if (! userOpt.isPresent )
            throw IllegalArgumentException("The user does not exist")
        val user = userOpt.get()
        user.isEnabled = false
        return userRepository.save(user).toDTO()
    }
}
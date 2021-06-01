package it.polito.ecommerce.integration

import it.polito.ecommerce.dto.LoginDTO
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.services.UserDetailsServiceExt
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@DataJpaTest(
    includeFilters = [ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = [Repository::class, Component::class, Service::class]
    )]
)
class UserDetailsServiceExtIntTests @Autowired constructor(
    private val service: UserDetailsServiceExt,
    ){
    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert loadUserByUsername successfully returns existing user`() {
        assert(service.loadUserByUsername("alice_in_wonderland").username == "alice_in_wonderland")
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert loadUserByUsername throws username not found if it does not exists`() {
        assertThrows<UsernameNotFoundException>{service.loadUserByUsername("VanishingCheshireCat")}
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert registerUser successfully registers new user`() {
        val registrationDTO = RegistrationDTO(
            username = "vanishing_cheshire_cat",
            password = "Cheshire_password1",
            confirmPassword = "Cheshire_password1",
            email = "cheshire_cat@mail.com"
        )
        val createdUser = service.registerUser(registrationDTO)
        assert(createdUser.username == "vanishing_cheshire_cat")
        assert(! createdUser.isEnabled)
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert addRole throws denied if user is not admin`() {
        assertThrows<AccessDeniedException>{service.addRole("alice_in_wonderland", "ADMIN").roles}
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert addRole successfully adds new role if called by admin`() {
        assert(service.addRole("alice_in_wonderland", "ADMIN").roles!!.contains("ADMIN"))
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert addRole throws IllegalArgumentException if user does not exists`() {
        assertThrows<IllegalArgumentException>{service.addRole("VanishingCheshireCat", "ADMIN")}
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert removeRole throws denied if user is not admin`() {
        assertThrows<AccessDeniedException>{service.removeRole("alice_in_wonderland", "ADMIN")}
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert removeRole successfully`() {

        //SAFE FROM TEST DONE BEFORE
        service.addRole("alice_in_wonderland", "ADMIN").roles

        assert( ! service.removeRole("alice_in_wonderland", "ADMIN").roles!!.contains("ADMIN") )
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert removeRole throws IllegalArgumentException if user does not exists`() {
        assertThrows<IllegalArgumentException>{service.removeRole("VanishingCheshireCat", "ADMIN")}
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert enableUser throws IllegalArgumentException if user does not exists`() {
        assertThrows<IllegalArgumentException>{service.enableUser("VanishingCheshireCat")}
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert enableUser successfully enables a user`() {
        assert(service.enableUser("alice_in_wonderland").isEnabled)
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert enableUser throws access denied exception if user is not admin`() {
        assertThrows<AccessDeniedException>{service.enableUser("alice_in_wonderland") }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert disableUser throws access denied exception if user is not admin`() {
        assertThrows<AccessDeniedException>{service.disableUser("alice_in_wonderland") }
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert disableUser successfully disables a user`() {
        service.enableUser("alice_in_wonderland")
        assert(!service.disableUser("alice_in_wonderland").isEnabled)
    }

    @Test
    @WithUserDetails(value = "admin")
    fun `Assert disableUser throws IllegalArgumentException if user does not exists`() {
        assertThrows<IllegalArgumentException>{service.disableUser("VanishingCheshireCat").isEnabled}
    }

    @Test
    fun `Assert verifyToken throws IllegalArgumentException if token does not exists`() {
        assertThrows<IllegalArgumentException>{service.verifyToken("token_that_does_not_exist")}
    }

    @Test
    fun `Assert verifyToken successfully verifies token if it exists and enables user`() {
        assertDoesNotThrow { service.verifyToken("token_alice") }
        assert ( service.loadUserByUsername("alice_in_wonderland").isEnabled )
    }

    @Test
    fun `Assert verifyToken throw IllegalArgumentException if token is expired`() {
        assertThrows<IllegalArgumentException>{service.verifyToken("token_alice_expired")}
    }

    @Test
    fun `Assert authAndCreateToken successfully returns a DTO with JWT token and updated roles`() {
        val inputLoginDTO = LoginDTO(
            username = "alice_in_wonderland",
            password = "Alices_password1",
            token = null,
            roles = null,
        )
        val loggedDTO = service.authAndCreateToken(inputLoginDTO)
        assert(loggedDTO.token!!.isNotEmpty())
        assert(loggedDTO.roles == mutableSetOf("CUSTOMER"))
    }

    @Test
    fun `Assert authAndCreateToken throws BadCredentialsException if user inputs the wrong password`() {
        val inputLoginDTO = LoginDTO(
            username = "alice_in_wonderland",
            password = "wrong_password",
            token = null,
            roles = null,
        )
        assertThrows<BadCredentialsException>{service.authAndCreateToken(inputLoginDTO)}
    }

}

package it.polito.ecommerce.unit

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkObject
import it.polito.ecommerce.domain.EmailVerificationToken
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.EmailVerificationTokenRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.security.JwtAuthenticationTokenFilter
import it.polito.ecommerce.security.JwtUtils
import it.polito.ecommerce.security.MethodSecurityConfig
import it.polito.ecommerce.services.MailServiceImpl
import it.polito.ecommerce.services.NotificationServiceImpl
import it.polito.ecommerce.services.UserDetailsServiceExtImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.sql.Timestamp
import java.util.*

//TODO EA


@WebMvcTest(
    UserDetailsServiceExtImpl::class,
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, value =
        [WebSecurityConfigurer::class, MethodSecurityConfig::class, JwtAuthenticationTokenFilter::class]
    )],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class]
)
class UserDetailsServiceExtUnitTests(@Autowired private val authServiceImpl: UserDetailsServiceExtImpl){

    @MockkBean
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var notificationService: NotificationServiceImpl

    @MockkBean
    private lateinit var verificationRepository: EmailVerificationTokenRepository

    @MockkBean
    private lateinit var authenticationManager: AuthenticationManager

    @MockkBean
    private lateinit var passwordEncoder: PasswordEncoder

    @MockkBean
    private lateinit var jwtUtils: JwtUtils

    @MockkBean
    private lateinit var mailService: MailServiceImpl


    private val user = User(
        username = "alice_in_wonderland",
        password = "{bcrypt}$2a$10\$PBp/YvYi0NoYnG/erDcn4uHSREj0cYmDAgl4yWD86mnSPcFRg1NMe",
        isEnabled = false,
        email = "alice_inwonderland@mail.com",
        roles = "CUSTOMER"
    )

    @Test
    fun `Assert loadUserByUsername returns correct user dto`() {
        mockkObject(user)
        every { user.getId() } returns 1L
        every {userRepository.findByUsername(any())} returns Optional.of(user)

        assert(authServiceImpl.loadUserByUsername("alice_in_wonderland") == user.toDTO())
    }

    @Test
    fun `Assert loadUserByUsername throws username not found if user doesn't exist`() {
        every {userRepository.findByUsername(any())} returns Optional.empty()

        assertThrows<UsernameNotFoundException> { authServiceImpl.loadUserByUsername("VanishingCheshireCat") }
    }

    @Test
    fun `Assert register user successfully registers a new user`() {
        val registrationDTO = RegistrationDTO(
            username = "alice_in_wonderland",
            password = "Alices_password1",
            confirmPassword = "Alices_password1",
            email = "alice_inwonderland@mail.com"
        )

        mockkObject(user)

        every { userRepository.save(any()) } returns user
        every { passwordEncoder.encode(any()) } returns "{bcrypt}$2a$10\$PBp/YvYi0NoYnG/erDcn4uHSREj0cYmDAgl4yWD86mnSPcFRg1NMe"
        every { notificationService.createEmailVerificationToken(any())} returns "token_alice"
        every { mailService.sendMessage( any(), any(), any() ) } returns Unit
        every { user.getId() } returns 1

        assert(authServiceImpl.registerUser(registrationDTO) == user.toDTO())
    }

    @Test
    fun `Assert addRole successfully adds new role`() {
        val tmpUser = User(
            username = "alice_in_wonderland",
            password = "{bcrypt}$2a$10\$PBp/YvYi0NoYnG/erDcn4uHSREj0cYmDAgl4yWD86mnSPcFRg1NMe",
            isEnabled = false,
            email = "alice_inwonderland@mail.com",
            roles = "CUSTOMER,ADMIN"
        )

        mockkObject(tmpUser)
        every { tmpUser.getId() } returns 1

        every { userRepository.findByUsername(user.username) } returns Optional.of(user)

        mockkObject(user)

        every { userRepository.save(any()) } returns user
        every { user.getId() } returns 1

        val userFetched = authServiceImpl.addRole(user.username, "ADMIN")

        assert(userFetched == tmpUser.toDTO())
    }

    @Test
    fun `Assert addRole throws illegal argument exception if username does not exists`() {
        every { userRepository.findByUsername(any()) } returns Optional.empty()

        assertThrows<IllegalArgumentException> { authServiceImpl.addRole("VanishingCat", "ADMIN") }
    }

    @Test
    fun `Assert removeRole successfully removes role`() {
        val tmpUser = User(
            username = "alice_in_wonderland",
            password = "{bcrypt}$2a$10\$PBp/YvYi0NoYnG/erDcn4uHSREj0cYmDAgl4yWD86mnSPcFRg1NMe",
            isEnabled = false,
            email = "alice_inwonderland@mail.com",
            roles = "CUSTOMER"
        )
        user.roles = "CUSTOMER,ADMIN"
        mockkObject(tmpUser)
        every { tmpUser.getId() } returns 1

        every { userRepository.findByUsername(user.username) } returns Optional.of(user)

        mockkObject(user)
        every { user.getId() } returns 1

        every { userRepository.save(any()) } returns user

        val userFetched = authServiceImpl.removeRole(user.username, "ADMIN")

        assert(userFetched == tmpUser.toDTO())
    }

    @Test
    fun `Assert removeRole throws illegal argument exception if username does not exists`() {
        every { userRepository.findByUsername(any()) } returns Optional.empty()

        assertThrows<IllegalArgumentException> { authServiceImpl.removeRole("VanishingCheshireCat", "ADMIN") }
    }

    @Test
    fun `Assert enableUser successfully enables a user`() {
        every { userRepository.findByUsername(user.username) } returns Optional.of(user)

        user.isEnabled = true
        mockkObject(user)
        every { user.getId() } returns 1

        every { userRepository.save(any()) } returns user

        val userFetched = authServiceImpl.enableUser(user.username)

        assert(userFetched == user.toDTO())
    }

    @Test
    fun `Assert enableUser throws illegal argument exception if username does not exists`() {
        every { userRepository.findByUsername(any()) } returns Optional.empty()

        assertThrows<IllegalArgumentException> { authServiceImpl.enableUser("VanishingCheshireCat") }
    }

    @Test
    fun `Assert disableUser successfully disables user`() {
        every { userRepository.findByUsername(user.username) } returns Optional.of(user)
        mockkObject(user)
        every { user.getId() } returns 1

        every { userRepository.save(any()) } returns user

        val userFetched = authServiceImpl.disableUser(user.username)

        assert(userFetched == user.toDTO())
    }

    @Test
    fun `Assert disableUser throws illegal argument exception if username does not exists`() {
        every { userRepository.findByUsername(any()) } returns Optional.empty()

        assertThrows<IllegalArgumentException> { authServiceImpl.disableUser("VanishingCheshireCat") }
    }

    @Test
    fun `Assert verifyToken successfully verifies token`() {
        val emailVerificationToken = EmailVerificationToken(
            Timestamp(System.currentTimeMillis() + 1000 * 60 * 60), // now() + 1h
            "token_alice",
            user
        )
        mockkObject(emailVerificationToken)
        mockkObject(user)
        every { verificationRepository.findByToken(any()) } returns Optional.of(emailVerificationToken)

        every { notificationService.createEmailVerificationToken(any())} returns "token_alice"

        every { userRepository.findByUsername(any())} returns Optional.of(user)
        every { userRepository.save(any())} returns user
        every { user.getId()} returns 1L

        assertDoesNotThrow { authServiceImpl.verifyToken("token_alice") }
    }

    @Test
    fun `Assert verifyToken throws illegal argument exception if token does not exists`() {
        every { verificationRepository.findByToken(any()) } returns Optional.empty()

        assertThrows<IllegalArgumentException> { authServiceImpl.verifyToken("alice_token") }
    }

    @Test
    fun `Assert verifyToken throws illegal argument exception if token exists but it is expired `() {
        val emailVerificationToken = EmailVerificationToken(
            Timestamp(System.currentTimeMillis() - 1000 * 60 * 60), // now() - 1h
            "token_alice",
            user
        )
        mockkObject(emailVerificationToken)

        every { verificationRepository.findByToken(any()) } returns Optional.of(emailVerificationToken)

        every { notificationService.createEmailVerificationToken(any())} returns "token_alice"

        assertThrows<IllegalArgumentException> { authServiceImpl.verifyToken("token_alice") }
    }

}

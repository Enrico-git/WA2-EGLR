package it.polito.ecommerce.unit

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import it.polito.ecommerce.controllers.AuthController
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.repositories.EmailVerificationTokenRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.security.JwtAuthenticationTokenFilter
import it.polito.ecommerce.security.JwtUtils
import it.polito.ecommerce.security.MethodSecurityConfig
import it.polito.ecommerce.services.MailServiceImpl
import it.polito.ecommerce.services.NotificationServiceImpl
import it.polito.ecommerce.services.UserDetailsServiceExtImpl
//import it.polito.ecommerce.services.WalletServiceImpl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.function.EntityResponse

@WebMvcTest(
    AuthController::class,
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
    [WebSecurityConfigurer::class, MethodSecurityConfig::class, JwtAuthenticationTokenFilter::class])],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class])
class AuthControllerUnitTests(@Autowired private val authController: AuthController) {

    @MockkBean
    private lateinit var userDetailsServiceExtImpl: UserDetailsServiceExtImpl

    @Test
    fun `Assert get wallet successfully retrieves existing wallet`(){
        val registrationDTO = RegistrationDTO(
            username = "pippo",
            password = "Pippos_password1",
            confirmPassword = "Pippos_password1",
            email = "pippo@mail.com"
        )
        val userDetailsDTO = UserDetailsDTO(
            1,
            "pippo",
            "bcrypt_hashed_pw",
            true,
            "a@b.com",
            "CUSTOMER"
        )
        val out = ResponseEntity(userDetailsDTO, HttpStatus.CREATED)
        every { userDetailsServiceExtImpl.registerUser(registrationDTO) } returns userDetailsDTO
        assert(authController.register(registrationDTO) == out)
    }
}
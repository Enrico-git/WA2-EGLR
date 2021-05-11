package it.polito.ecommerce.unit

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import it.polito.ecommerce.controllers.AuthController
import it.polito.ecommerce.dto.LoginDTO
import it.polito.ecommerce.dto.RegistrationDTO
import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.security.JwtAuthenticationTokenFilter
import it.polito.ecommerce.security.MethodSecurityConfig
import it.polito.ecommerce.services.UserDetailsServiceExtImpl
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration

@WebMvcTest(
    AuthController::class,
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, value =
        [WebSecurityConfigurer::class, MethodSecurityConfig::class, JwtAuthenticationTokenFilter::class]
    )],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class]
)
class AuthControllerUnitTests(@Autowired private val authController: AuthController) {

    @MockkBean
    private lateinit var userDetailsServiceExtImpl: UserDetailsServiceExtImpl

    @Test
    fun `Assert register user successfully creates a new user`() {
        val registrationDTO = RegistrationDTO(
            username = "pippo",
            password = "Pippos_password1",
            confirmPassword = "Pippos_password1",
            email = "pippo@gmail.com"
        )
        val userDetailsDTO = UserDetailsDTO(
            id = 1,
            username = "pippo",
            password = "bcrypt_hashed_pw",
            isEnabled = true,
            email = "a@b.com",
            roles = "CUSTOMER"
        )
        val out = ResponseEntity(userDetailsDTO, HttpStatus.CREATED)
        every { userDetailsServiceExtImpl.registerUser(registrationDTO) } returns userDetailsDTO
        assert(authController.register(registrationDTO) == out)
    }

    @Test
    fun `Assert sign in successfully signes in the user`() {
        val loginDTO = LoginDTO(
            token = null,
            username = "alice_in_wonderland",
            roles = null,
            password = "Alices_password1"
        )

        val result = LoginDTO(
            token = "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6IkNVU1RPTUVSIiwic3ViIjoiYWxpY2VfaW5fd29uZGVybGFuZCJ9.v6pemVBDA21ywInnvV3QRYdl3Vtnr4FwGt14WV67U_ntAOli389H0BR5NWJPrY4Fx82aKC7GQEBoAuUZpa4_8A",
            username = "alice_in_wonderland",
            roles = mutableSetOf("CUSTOMER"),
            password = "Alices_password1"
        )

        val out = ResponseEntity(result, HttpStatus.OK)
        every { userDetailsServiceExtImpl.authAndCreateToken(loginDTO) } returns result
        assert(authController.signIn(loginDTO) == out)

    }

//    @Test
//    fun `Assert register user throws Data Integrity Violation Exception if user still exists`(){
//        val registrationDTO = RegistrationDTO(
//            username = "alice_in_wonderland",
//            password = "Alices_password1",
//            confirmPassword = "Alices_password1",
//            email = "alice@gmail.com"
//        )
//        /*val userDetailsDTO = UserDetailsDTO(
//            id = 1,
//            username = "alice_in_wonderland",
//            password = "bcrypt_hashed_pw",
//            isEnabled = true,
//            email = "a@b.com",
//            roles = "CUSTOMER"
//        )*/
//        /*val out = ResponseEntity(userDetailsDTO, HttpStatus.CREATED)*/
//        every { userDetailsServiceExtImpl.registerUser(registrationDTO) } returns userDetailsDTO
//        assertThrows<DataIntegrityViolationException> { userDetailsServiceExtImpl.registerUser(registrationDTO) }
//
//        //assert(authController.register(registrationDTO) == out)
//    }


}
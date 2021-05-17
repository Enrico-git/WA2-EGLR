package it.polito.ecommerce.integration

import it.polito.ecommerce.security.JwtUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.web.context.WebApplicationContext


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DataJpaTest(includeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [Repository::class, Component::class, Service::class, Configuration::class])])
class WalletControllerIntTests @Autowired constructor(
    private val context : WebApplicationContext,
    private val jwtUtils: JwtUtils,
    private val authenticationManager : AuthenticationManager
) {
    lateinit var mockMvc : MockMvc
    lateinit var token : String

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply<DefaultMockMvcBuilder>(springSecurity()).build()
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken("alice_in_wonderland", "Alices_password1"))
        token = jwtUtils.generateJwtToken(authentication)
    }

    @Test
    fun `Alice can retrieve her wallet`(){
        mockMvc.perform(get("/wallet/7").header("Authorization", "Bearer $token"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(7))
    }

    @Test
    fun `Alice cannot retrieve other customers wallet`(){
        mockMvc.perform(get("/wallet/8").header("Authorization", "Bearer $token"))
            .andExpect(status().isForbidden)
    }
}
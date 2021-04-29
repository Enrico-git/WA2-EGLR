package it.polito.ecommerce.unit

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.Customer
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.TransactionRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.security.JwtAuthenticationTokenFilter
import it.polito.ecommerce.security.MethodSecurityConfig
import it.polito.ecommerce.services.WalletServiceImpl
import javassist.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


//@SpringBootTest
//@ExtendWith(SpringExtension::class)
//@ExtendWith(SpringExtension::class)
//@EnableAutoConfiguration(exclude = [SecurityAutoConfiguration::class])
@WebMvcTest(WalletServiceImpl::class,
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
    [WebSecurityConfigurer::class, MethodSecurityConfig::class, JwtAuthenticationTokenFilter::class])],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class])
class WalletServiceUnitTests(@Autowired private val walletServiceImpl: WalletServiceImpl){


//    @MockkBean
//    private lateinit var walletServiceImpl: WalletServiceImpl
    @MockkBean
    private lateinit var walletRepository: WalletRepository

    @MockkBean
    private lateinit var customerRepository: CustomerRepository

    @MockkBean
    private lateinit var transactionRepository: TransactionRepository

    @Test
    fun `Assert get wallet successfully retrieves existing wallet`(){
        val wallet = Wallet(
            customer = Customer(
                    name = "Alice",
                    surname = "Wonderland",
                    address = "Rabbit hole, 15",
                    email = "alice_inwonderland@mail.com",
                    user = User(
                        username = "alice",
                        password = "my_salted_pw",
                        email = "alice_inwonderland@mail.com",
                        roles = Rolename.CUSTOMER.toString()
                    )
            )
        )

        every { walletRepository.findById(1L) } returns Optional.of(wallet)

        val walletFetched = walletServiceImpl.getWallet(1L)

        assert(walletFetched == wallet.toDTO())

    }

    @Test
    fun `Assert get wallet throws not found exception if wallet missing in db`(){

        every { walletRepository.findById(1L) } returns Optional.empty()

        assertThrows<NotFoundException> { walletServiceImpl.getWallet(1L) }

    }
}
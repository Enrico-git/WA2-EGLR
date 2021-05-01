package it.polito.ecommerce.integration

import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.services.UserDetailsServiceExtImpl
import it.polito.ecommerce.services.WalletService
import javassist.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.stereotype.Service

//@SpringBootTest
@Transactional
@DataJpaTest(includeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [Repository::class, Component::class, Service::class])])
//@ExtendWith(SpringExtension::class)
//@TestPropertySource(properties = ["classpath:application.properties"])
class WalletServiceIntTests @Autowired constructor(
    private val service: WalletService,
    private val repo: WalletRepository,
    private val customerRepository: CustomerRepository
){

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert get wallet successfully retrieves correct wallet`() {
        assert(service.getWallet(7).customerID == 4L)
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert get wallet doesnt retrieve wallet if user is not owner`() {
        assertThrows<Exception> { service.getWallet(8).customerID}
    }

}
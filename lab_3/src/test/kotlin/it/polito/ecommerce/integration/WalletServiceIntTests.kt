package it.polito.ecommerce.integration

import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.services.WalletService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

//@SpringBootTest
@Transactional
@DataJpaTest(includeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, classes = [Repository::class, Component::class])])
//@ExtendWith(SpringExtension::class)
//@TestPropertySource(properties = ["classpath:application.properties"])
class WalletServiceIntTests @Autowired constructor(
    private val service: WalletService,
    private val repo: WalletRepository,
    private val customerRepository: CustomerRepository
){

    @Test
    fun `Assert get wallet successfully retrieves correct wallet`() {
        assert(service.getWallet(7).customerID == 4L)
    }

}
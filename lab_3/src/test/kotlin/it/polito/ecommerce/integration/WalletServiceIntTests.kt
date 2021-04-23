package it.polito.ecommerce.integration

import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.services.WalletService
import javassist.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class WalletServiceIntTests @Autowired constructor(
    private val service: WalletService,
    private val repo: WalletRepository,
    private val customerRepository: CustomerRepository
) {

    @Test
    fun `Assert get wallet successfully retrieves correct wallet`() {
        assert(service.getWallet(7).customerID == 4L)
    }

}
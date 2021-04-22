package it.polito.ecommerce.integration

import it.polito.ecommerce.services.WalletService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WalletServiceImplTests @Autowired constructor(
    private val service: WalletService,
) {
    @Test
    fun `Assert get wallet successfully retrieves correct wallet`() {
        assert(service.getWallet(7).customerID == 4L)
    }

}
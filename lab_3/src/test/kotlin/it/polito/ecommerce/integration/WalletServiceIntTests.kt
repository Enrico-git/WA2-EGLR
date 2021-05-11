package it.polito.ecommerce.integration

import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.CreateWalletDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.security.JwtAuthenticationTokenFilter
import it.polito.ecommerce.security.MethodSecurityConfig
import it.polito.ecommerce.services.UserDetailsServiceExtImpl
import it.polito.ecommerce.services.WalletService
import it.polito.ecommerce.services.WalletServiceImpl
import javassist.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.math.BigDecimal
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import java.sql.Timestamp
import kotlinx.coroutines.*

//@SpringBootTest
@Transactional
@DataJpaTest(
    includeFilters = [ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = [Repository::class, Component::class, Service::class]
    )]
)
//@ExtendWith(SpringExtension::class)
//@TestPropertySource(properties = ["classpath:application.properties"])
class WalletServiceIntTests @Autowired constructor(
    private val service: WalletService,
    private val walletRepository: WalletRepository
) {

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert get wallet successfully retrieves correct wallet`() {
        assert(service.getWallet(7).customerID == 4L)
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert get wallet throws access denied if user is not owner`() {
        assertThrows<AccessDeniedException> { service.getWallet(8).customerID }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert add wallet successfully adds wallet to database`() {
        val createWalletDTO = CreateWalletDTO(id = 4)
        assert(service.addWallet(createWalletDTO).id != null)
        assert(service.addWallet(createWalletDTO).customerID == 4L)
        assert(service.addWallet(createWalletDTO).balance == BigDecimal(0))
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert add wallet is forbidden if user is not the correct customer`() {
        val createWalletDTO = CreateWalletDTO(id = 5)
        assertThrows<AccessDeniedException> { service.addWallet(createWalletDTO) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert get wallet transactions returns a list of transactions if parameters are valid`() {
        var pageable = PageRequest.of(0, 2)
        val t1 = System.currentTimeMillis()
        var transactions = service.getWalletTransactions(7, 0, t1, pageable)
        assert(transactions.size == 2)
        assert(transactions[0].id == 10L)
        assert(transactions[0].timestamp!! <= Timestamp(t1) && transactions[0].timestamp!! >= Timestamp(0))
        assert(transactions[1].id == 11L)
        assert(transactions[1].timestamp!! <= Timestamp(t1) && transactions[1].timestamp!! >= Timestamp(0))
        pageable = PageRequest.of(0, 1)
        val t2 = 1000 * 60 * 60 * 24L // 1 day
        transactions = service.getWalletTransactions(7, -t2, t2, pageable)
        assert(transactions.size == 1)
        assert(transactions[0].id == 12L)
        assert(transactions[0].timestamp!! <= Timestamp(t2) && transactions[0].timestamp!! >= Timestamp(-t2))

        assert(
            service.getWalletTransactions(7, -1000 * 60 * 60 * 24 * 11, -1000 * 60 * 60 * 24 * 10, pageable).isEmpty()
        )
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert get wallet transactions returns all the transactions if the time range is not specified`() {
        val pageable = PageRequest.of(0, 2)
        val transactions = service.getWalletTransactions(7, null, null, pageable)
        assert(transactions.size == 2)
        assert(transactions[0].id == 10L)
        assert(transactions[1].id == 11L)
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert get wallet transactions throws access denied exception if user is not owner of wallet`() {
        val pageable = PageRequest.of(0, 2)
        assertThrows<AccessDeniedException> { service.getWalletTransactions(8, null, null, pageable) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert get wallet single transaction successfully returns a transaction dto if transaction exists`() {
        assertDoesNotThrow { service.getWalletSingleTransaction(7L, 12L) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert get wallet single transaction throws not found exception if transaction does not exist`() {
        assertThrows<NotFoundException> { service.getWalletSingleTransaction(7L, 200L) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert get wallet single transaction throws access denied exception if user is not owner of wallet`() {
        assertThrows<AccessDeniedException> { service.getWalletSingleTransaction(8L, 200L) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert perform transaction successfully adds transaction to db`() {
        val transactionDTO = TransactionDTO(
            id = null,
            senderID = 7,
            receiverID = 8,
            timestamp = null,
            amount = BigDecimal(20)
        )
        var wallets = walletRepository.findAllById(mutableSetOf(7L, 8L))
        val senderBalanceBefore = wallets.find { it.getId() == 7L }!!.balance
        val receiverBalanceBefore = wallets.find { it.getId() == 8L }!!.balance

        val result = service.performTransaction(transactionDTO)

        wallets = walletRepository.findAllById(mutableSetOf(7L, 8L))
        val senderBalanceAfter = wallets.find { it.getId() == 7L }!!.balance
        val receiverBalanceAfter = wallets.find { it.getId() == 8L }!!.balance

        assert(result.id != null && result.amount == BigDecimal(20) && result.timestamp != null)
        assert(senderBalanceAfter == senderBalanceBefore - BigDecimal(20))
        assert(receiverBalanceAfter == receiverBalanceBefore + BigDecimal(20))
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert perform transaction throws Illegal argument exception if receiver wallet doesn't exist`() {
        val transactionDTO = TransactionDTO(
            id = null,
            senderID = 7,
            receiverID = 12,
            timestamp = null,
            amount = BigDecimal(20)
        )
        assertThrows<IllegalArgumentException> { service.performTransaction(transactionDTO) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert perform transaction throws Illegal argument exception if sender's balance is not high enough`() {
        val transactionDTO = TransactionDTO(
            id = null,
            senderID = 7,
            receiverID = 8,
            timestamp = null,
            amount = BigDecimal(200000)
        )
        assertThrows<IllegalArgumentException> { service.performTransaction(transactionDTO) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert perform transaction throws access denied exception if user is not sender's wallet owner`() {
        val transactionDTO = TransactionDTO(
            id = null,
            senderID = 9,
            receiverID = 8,
            timestamp = null,
            amount = BigDecimal(200000)
        )
        assertThrows<AccessDeniedException> { service.performTransaction(transactionDTO) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `assert concurrent calls of perform transaction yield correct results`() = runBlocking<Unit> {

        val transactionDTO = TransactionDTO(
            id = null,
            senderID = 7,
            receiverID = 8,
            timestamp = null,
            amount = BigDecimal(20)
        )

        val transactionDTO2 = TransactionDTO(
            id = null,
            senderID = 7,
            receiverID = 9,
            timestamp = null,
            amount = BigDecimal(20)
        )

        var wallets = walletRepository.findAllById(mutableSetOf(7L, 8L, 9L))
        val senderBalanceBefore = wallets.find { it.getId() == 7L }!!.balance
        val receiverBalanceBefore = wallets.find { it.getId() == 8L }!!.balance
        val receiverBalanceBefore2 = wallets.find { it.getId() == 9L }!!.balance

        val a1 = async { doTransaction(transactionDTO) }
        val a2 = async { doTransaction(transactionDTO2) }
        val t2 = a2.await()
        val t1 = a1.await()

        wallets = walletRepository.findAllById(mutableSetOf(7L, 8L, 9L))
        val senderBalanceAfter = wallets.find { it.getId() == 7L }!!.balance
        val receiverBalanceAfter = wallets.find { it.getId() == 8L }!!.balance
        val receiverBalanceAfter2 = wallets.find { it.getId() == 9L }!!.balance

        assert(t1.id != null && t1.amount == BigDecimal(20) && t1.timestamp != null)
        assert(t2.id != null && t2.amount == BigDecimal(20) && t2.timestamp != null)
        assert(senderBalanceAfter == senderBalanceBefore - BigDecimal(40))
        assert(receiverBalanceAfter == receiverBalanceBefore + BigDecimal(20))
        assert(receiverBalanceAfter2 == receiverBalanceBefore2 + BigDecimal(20))

    }

    suspend fun doTransaction(transactionDTO: TransactionDTO): TransactionDTO {
        return service.performTransaction(transactionDTO)
    }

}
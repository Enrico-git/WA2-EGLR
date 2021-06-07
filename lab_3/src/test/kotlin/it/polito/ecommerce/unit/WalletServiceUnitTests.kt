package it.polito.ecommerce.unit

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkObject
import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.Customer
import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.CreateWalletDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.TransactionRepository
import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.security.JwtAuthenticationTokenFilter
import it.polito.ecommerce.security.MethodSecurityConfig
import it.polito.ecommerce.services.WalletServiceImpl
import javassist.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.domain.PageRequest
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.sql.Timestamp
import java.util.*


//@SpringBootTest
//@ExtendWith(SpringExtension::class)
//@ExtendWith(SpringExtension::class)
//@EnableAutoConfiguration(exclude = [SecurityAutoConfiguration::class])
@WebMvcTest(
    WalletServiceImpl::class,
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, value =
        [WebSecurityConfigurer::class, MethodSecurityConfig::class, JwtAuthenticationTokenFilter::class]
    )],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class]
)
class WalletServiceUnitTests(@Autowired private val walletServiceImpl: WalletServiceImpl) {

    @MockkBean
    private lateinit var walletRepository: WalletRepository

    @MockkBean
    private lateinit var customerRepository: CustomerRepository

    @MockkBean
    private lateinit var transactionRepository: TransactionRepository

    private val alice_customer = Customer(
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


    private val wallet = Wallet(customer = alice_customer)

    private val transaction = Transaction(Timestamp(System.currentTimeMillis()), wallet, wallet, BigDecimal(20))

    @Test
    fun `Assert get wallet successfully retrieves existing wallet`() {

        every { walletRepository.findById(1L) } returns Optional.of(wallet)

        val walletFetched = walletServiceImpl.getWallet(1L)

        assert(walletFetched == wallet.toDTO())

    }

    @Test
    fun `Assert get wallet throws not found exception if wallet missing in db`() {

        every { walletRepository.findById(1L) } returns Optional.empty()

        assertThrows<NotFoundException> { walletServiceImpl.getWallet(1L) }

    }

    @Test
    fun `Assert add wallet successfully adds wallet to customer`() {
        val createWalletDTO = CreateWalletDTO(
            id = 1
        )
        val out = Wallet(BigDecimal(0), alice_customer)
        val aliceOpt = Optional.of(alice_customer)
        every { customerRepository.findById(1) } returns aliceOpt

        every { walletRepository.save(any()) } returns out
        assert(walletServiceImpl.addWallet(createWalletDTO) == out.toDTO())
    }

    @Test
    fun `Assert add wallet throws exception if no customer is found`() {
        val createWalletDTO = CreateWalletDTO(
            id = 1
        )
        val out = Wallet(BigDecimal(0), alice_customer)
        every { customerRepository.findById(1) } returns Optional.empty()
        every { walletRepository.save(any()) } returns out
        assertThrows<IllegalArgumentException> { walletServiceImpl.addWallet(createWalletDTO) }
    }

    @Test
    fun `Assert get wallet transactions successfully retrieves transactions if time range is provided`() {
        val pageable = PageRequest.of(0, 2)
        val t2 = Timestamp(20)
        val t3 = Timestamp(30)
        val out = listOf(transaction, transaction)
        every { walletRepository.findById(any()) } returns Optional.of(wallet)
        every {
            transactionRepository.findAllByWalletAndByTimestampBetween(wallet, t2, t3, pageable)
        } returns out

        assert(walletServiceImpl.getWalletTransactions(1, 20, 30, pageable) == out.map { it.toDTO() })

    }

    @Test
    fun `Assert get wallet transactions throws exception if wallet is not found`() {
        val pageable = PageRequest.of(0, 2)
        every { walletRepository.findById(any()) } returns Optional.empty()
        assertThrows<IllegalArgumentException> { walletServiceImpl.getWalletTransactions(1, 20, 30, pageable) }
    }

    @Test
    fun `Assert get wallet transactions throws exception if the time range is not correctly provided`() {
        val pageable = PageRequest.of(0, 2)
        every { walletRepository.findById(any()) } returns Optional.of(wallet)

        assertThrows<IllegalArgumentException> { walletServiceImpl.getWalletTransactions(1, 20, null, pageable) }
        assertThrows<IllegalArgumentException> { walletServiceImpl.getWalletTransactions(1, null, 20, pageable) }
    }

    @Test
    fun `Assert get wallet transactions successfully retrieves transactions if time range is NOT provided`() {
        val pageable = PageRequest.of(0, 2)
        val out = listOf(transaction, transaction)
        every { walletRepository.findById(any()) } returns Optional.of(wallet)
        every {
            transactionRepository.findAllByWallet(wallet, pageable)
        } returns out

        assert(walletServiceImpl.getWalletTransactions(1, null, null, pageable) == out.map { it.toDTO() })

    }

    @Test
    fun `Assert get wallet single transaction returns single transaction if exists`() {
        every { walletRepository.findById(any()) } returns Optional.of(wallet)
        every { transactionRepository.findByWalletAndId(any(), any()) } returns Optional.of(transaction)

        assert(walletServiceImpl.getWalletSingleTransaction(1, 2) == transaction.toDTO())
    }

    @Test
    fun `Assert get wallet single transaction throws illegal argument exception if the wallet does not exist`() {
        every { walletRepository.findById(any()) } returns Optional.empty()
        assertThrows<IllegalArgumentException> { walletServiceImpl.getWalletSingleTransaction(1, 2) }
    }

    @Test
    fun `Assert get wallet single transaction throws not found exception if the transaction does not exist`() {
        every { walletRepository.findById(any()) } returns Optional.of(wallet)
        every { transactionRepository.findByWalletAndId(any(), any()) } returns Optional.empty()
        assertThrows<NotFoundException> { walletServiceImpl.getWalletSingleTransaction(1, 2) }
    }

    @Test
    fun `Assert perform transaction returns transaction dto if called with valid parameters`() {
        val senderWallet = Wallet(BigDecimal(100), customer = alice_customer)
        mockkObject(senderWallet)
        val receiverWallet = Wallet(BigDecimal(80), customer = alice_customer)
        mockkObject(receiverWallet)
        every { walletRepository.findAllById(mutableSetOf(1, 2)) } returns mutableListOf(senderWallet, receiverWallet)
        every { senderWallet.getId() } returns 1
        every { receiverWallet.getId() } returns 2
        every { walletRepository.saveAndFlush(senderWallet) } returns senderWallet
        every { walletRepository.saveAndFlush(receiverWallet) } returns receiverWallet
        every { transactionRepository.save(any()) } returns transaction

        assert(
            walletServiceImpl.performTransaction(
                TransactionDTO(
                    null,
                    1,
                    2,
                    Timestamp(1),
                    BigDecimal(20)
                )
            ) == transaction.toDTO()
        )
    }

    @Test
    fun `Assert perform transaction throws exception if a sender and receiver are equal`() {
        assertThrows<IllegalArgumentException> {
            walletServiceImpl.performTransaction(
                TransactionDTO(
                    null,
                    1,
                    1,
                    Timestamp(1),
                    BigDecimal(20)
                )
            )
        }
    }

    @Test
    fun `Assert perform transaction throws illegal argument exception if sender or receiver wallets are not found`() {
        val senderWallet = Wallet(BigDecimal(100), customer = alice_customer)
        mockkObject(senderWallet)
        val receiverWallet = Wallet(BigDecimal(80), customer = alice_customer)
        mockkObject(receiverWallet)
        every { walletRepository.findAllById(mutableSetOf(1, 2)) } returns mutableListOf(receiverWallet)
        every { senderWallet.getId() } returns 1
        every { receiverWallet.getId() } returns 2
        assertThrows<IllegalArgumentException> {
            walletServiceImpl.performTransaction(
                TransactionDTO(
                    null,
                    1,
                    2,
                    Timestamp(1),
                    BigDecimal(20)
                )
            )
        }
        every { walletRepository.findAllById(mutableSetOf(1, 2)) } returns mutableListOf(senderWallet)
        assertThrows<IllegalArgumentException> {
            walletServiceImpl.performTransaction(
                TransactionDTO(
                    null,
                    1,
                    2,
                    Timestamp(1),
                    BigDecimal(20)
                )
            )
        }
        every { walletRepository.findAllById(mutableSetOf(1, 2)) } returns mutableListOf()
        assertThrows<IllegalArgumentException> {
            walletServiceImpl.performTransaction(
                TransactionDTO(
                    null,
                    1,
                    2,
                    Timestamp(1),
                    BigDecimal(20)
                )
            )
        }

    }

    @Test
    fun `Assert perform transaction throws illegal argument exception if sender balance is not high enough`() {
        val senderWallet = Wallet(BigDecimal(100), customer = alice_customer)
        mockkObject(senderWallet)
        val receiverWallet = Wallet(BigDecimal(80), customer = alice_customer)
        mockkObject(receiverWallet)
        every { walletRepository.findAllById(mutableSetOf(1, 2)) } returns mutableListOf(senderWallet, receiverWallet)
        every { senderWallet.getId() } returns 1
        every { receiverWallet.getId() } returns 2
        assertThrows<IllegalArgumentException> {
            walletServiceImpl.performTransaction(
                TransactionDTO(
                    null,
                    1,
                    2,
                    Timestamp(1),
                    BigDecimal(120)
                )
            )
        }
    }
}
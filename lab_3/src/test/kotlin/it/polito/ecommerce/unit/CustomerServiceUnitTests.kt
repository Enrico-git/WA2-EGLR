package it.polito.ecommerce.unit
//TODO EA
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkObject
import it.polito.ecommerce.common.Rolename
import it.polito.ecommerce.domain.Customer
import it.polito.ecommerce.domain.User
import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.security.JwtAuthenticationTokenFilter
import it.polito.ecommerce.security.MethodSecurityConfig
import it.polito.ecommerce.services.CustomerServiceImpl
import javassist.NotFoundException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import java.util.*

@WebMvcTest(
    CustomerServiceImpl::class,
    excludeFilters = [ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, value =
        [WebSecurityConfigurer::class, MethodSecurityConfig::class, JwtAuthenticationTokenFilter::class]
    )],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class, SecurityFilterAutoConfiguration::class]
)
class CustomerServiceUnitTests(@Autowired private val customerServiceImpl: CustomerServiceImpl) {
    @MockkBean
    private lateinit var customerRepository: CustomerRepository

    @MockkBean
    private lateinit var userRepository: UserRepository

    private val user = User(
        username = "alice",
        password = "my_salted_pw",
        email = "alice_inwonderland@mail.com",
        roles = Rolename.CUSTOMER.toString()
    )

    private val customer = Customer(
        name = "Alice",
        surname = "Pleasance Liddell",
        address = "Wonderland, 2",
        email = "alice_inwonderland@mail.com",
        user = User(
            username = "alice",
            password = "my_salted_pw",
            email = "alice_inwonderland@mail.com",
            roles = Rolename.CUSTOMER.toString()
        )
    )

    private val customerDTO = CustomerDTO(
        id = 4,
        name = "Alice",
        surname = "Pleasance Liddell",
        address = "Wonderland, 2",
        email = "alice_inwonderland@mail.com",
        userID = 1,
    )

    @Test
    fun `Assert get customer successfully retrieves existing customer`() {
        mockkObject(customer)
        every { customerRepository.findById(1L) } returns Optional.of(customer)
        every { customer.getId() } returns 4L
        every { customer.user.getId() } returns 1L
        val customerFetched = customerServiceImpl.getCustomer(1L)

        assert(customerFetched == customer.toDTO())
    }

    @Test
    fun `Assert get customer throws not found exception if customer not exists`() {

        every { customerRepository.findById(69L) } returns Optional.empty()

        assertThrows<NotFoundException> { customerServiceImpl.getCustomer(69L) }
    }

    @Test
    fun `Assert add customer successfully adds a new customer`() {
        mockkObject(customer)
        every { customer.getId() } returns 4L
        every { customer.user.getId() } returns 1L
        every { userRepository.findById(1L) } returns Optional.of(user)
        every { customerRepository.save(any()) } returns customer

        assert(customerServiceImpl.addCustomer(customerDTO) == customer.toDTO())
    }

    @Test
    fun `Assert add customer throws illegal argument exception if user doesn't exists`() {
        every { userRepository.findById(69L) } returns Optional.empty()

        val fakeCustomerDTO = CustomerDTO(
            id = 4,
            name = "Alice",
            surname = "Pleasance Liddell",
            address = "Wonderland, 2",
            email = "alice_inwonderland@mail.com",
            userID = 69,
        )

        assertThrows<IllegalArgumentException> { customerServiceImpl.addCustomer(fakeCustomerDTO) }
    }

    @Test
    fun `Assert update customer successfully updates the existing customer`() {
        mockkObject(customer)
        every { customer.getId() } returns 4L
        every { customer.user.getId() } returns 1L
        every { customerRepository.findById(4L) } returns Optional.of(customer)
        every { customerRepository.save(any()) } returns customer

        val updateCustomerDTO = CustomerDTO(
            id = 4,
            name = "Alice69",
            surname = "Pleasance Liddell69",
            address = "Wonderland, 269",
            email = "alice_inwonderland69@mail.com",
            userID = 1,
        )

        val outCustomerDTO  = CustomerDTO(
            id = 4,
            name = "Alice69",
            surname = "Pleasance Liddell69",
            address = "Wonderland, 269",
            email = "alice_inwonderland69@mail.com",
            userID = 1,
        )

        assert(customerServiceImpl.updateCustomer(updateCustomerDTO, 4L) == outCustomerDTO)
    }

    @Test
    fun `Assert update customer throws illegal argument exception if customer doesn't exists`() {
        every { customerRepository.findById(69L) } returns Optional.empty()

        val fakeCustomerDTO = CustomerDTO(
            id = 4,
            name = "Alice",
            surname = "Pleasance Liddell",
            address = "Wonderland, 2",
            email = "alice_inwonderland@mail.com",
            userID = 69,
        )

        assertThrows<IllegalArgumentException> { customerServiceImpl.updateCustomer(fakeCustomerDTO, 69L) }
    }

}


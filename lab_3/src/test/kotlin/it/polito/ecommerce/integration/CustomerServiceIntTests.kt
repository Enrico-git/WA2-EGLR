package it.polito.ecommerce.integration

//TODO EA

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.services.CustomerService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@DataJpaTest(
    includeFilters = [ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = [Repository::class, Component::class, Service::class]
    )]
)
class CustomerServiceIntTests @Autowired constructor(
    private val service: CustomerService
){

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert get customer successfully returns existing customer`() {
        assert(service.getCustomer(4L).id == 4L)
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert get customer throws access denied if user tries to access another user's wallet`() {
        assertThrows<AccessDeniedException> { service.getCustomer(69L).id }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert add customer successfully adds new user`() {
        val customerDTO = CustomerDTO(
            id = null,
            name = "Alice",
            surname = "Pleasance Liddell",
            address = "Wonderland, 2",
            email = "alice_inwonderland@mail.com",
            userID = 1,
        )

        val addedCustomerDTO = service.addCustomer(customerDTO)
        assert(addedCustomerDTO.id != null )
        assert(addedCustomerDTO.userID == 1L)
        assert(addedCustomerDTO.email == "alice_inwonderland@mail.com")
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert add customer is forbidden if user is not correct`() {
        val fakeCustomerDTO = CustomerDTO(
            id = null,
            name = "Alice",
            surname = "Pleasance Liddell",
            address = "Wonderland, 2",
            email = "alice_inwonderland@mail.com",
            userID = 69,
        )
        assertThrows<AccessDeniedException> { service.addCustomer(fakeCustomerDTO) }
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert update customer successfully updates the customer`() {
        val updateCustomerDTO = CustomerDTO(
            id = 4,
            name = "Alice69",
            surname = "Pleasance Liddell69",
            address = "Wonderland, 269",
            email = "alice_inwonderland69@mail.com",
            userID = 1,
        )
        val updatedCustomer = service.updateCustomer(updateCustomerDTO, 4)
        assert(updatedCustomer.id != null)
        assert(updatedCustomer.userID == 1L)
        assert(updatedCustomer.email == "alice_inwonderland69@mail.com")
    }

    @Test
    @WithUserDetails(value = "alice_in_wonderland")
    fun `Assert update customer is forbidden if user is not correct`() {
        val fakeCustomerDTO = CustomerDTO(
            id = null,
            name = "Alice69",
            surname = "Pleasance Liddell69",
            address = "Wonderland, 269",
            email = "alice_inwonderland69@mail.com",
            userID = 69,
        )
        assertThrows<AccessDeniedException> { service.updateCustomer(fakeCustomerDTO, 69) }
    }
}
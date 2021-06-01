package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Customer
import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.UserRepository
import javassist.NotFoundException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository
) : CustomerService {

    override fun addCustomer(customerDTO: CustomerDTO): CustomerDTO {
        val userOpt = userRepository.findById(customerDTO.userID)

        if (!userOpt.isPresent)
            throw IllegalArgumentException("User does not exist")

        val customer = Customer(
            name = customerDTO.name,
            surname = customerDTO.surname,
            address = customerDTO.address,
            email = customerDTO.email,
            user = userOpt.get()
        )
        return customerRepository.save(customer).toDTO()
    }

    override fun getCustomer(customerID: Long): CustomerDTO {
        val customerOpt = customerRepository.findById(customerID)
        if (!customerOpt.isPresent)
            throw NotFoundException("Customer not found")
        val customer = customerOpt.get()
        return customer.toDTO()
    }

    override fun updateCustomer(customerDTO: CustomerDTO, customerID: Long): CustomerDTO {
        val customerOpt = customerRepository.findById(customerID)

        if (!customerOpt.isPresent)
            throw IllegalArgumentException("Customer does not exist")

        val customer = customerOpt.get()
        customer.address = customerDTO.address
        customer.name = customerDTO.name
        customer.surname = customerDTO.surname
        customer.email = customerDTO.email
        return customerRepository.save(customer).toDTO()
    }
}
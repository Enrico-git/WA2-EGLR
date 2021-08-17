package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.Customer
import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.CustomerDTO
import it.polito.wa2.catalogservice.dto.toDTO
import it.polito.wa2.catalogservice.exceptions.NotFoundException
import it.polito.wa2.catalogservice.repositories.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

//TODO which service contacts the db?
@Service
@Transactional
class CustomerServiceImpl(
    //private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository
): CustomerService {

    override suspend fun addCustomer(customerDTO: CustomerDTO): CustomerDTO {
        /*val userOpt = userRepository.findById(customerDTO.userID)

        if (userOpt == null)
            throw IllegalArgumentException("User does not exist")

        val customer = Customer(
            name = customerDTO.name,
            surname = customerDTO.surname,
            address = customerDTO.address,
            email = customerDTO.email,
            user = userOpt.get()
        )
        return customerRepository.save(customer).toDTO()*/
        return Customer(null,"","","","", null).toDTO()
    }

    override suspend fun getCustomer(customerID: ObjectId): CustomerDTO {
        /*val customerOpt = customerRepository.findById(customerID)
        if (customerOpt == null)
            throw NotFoundException("Customer not found")
        val customer = customerOpt.get()
        return customer.toDTO()*/
        return Customer(null,"","","","", null).toDTO()
    }

    override suspend fun updateCustomer(customerDTO: CustomerDTO, customerID: ObjectId): CustomerDTO {
        /*val customerOpt = customerRepository.findById(customerID)

        if (customerOpt == null)
            throw IllegalArgumentException("Customer does not exist")

        customerOpt.address = customerDTO.address
        customerOpt.name = customerDTO.name
        customerOpt.surname = customerDTO.surname
        customerOpt.email = customerDTO.email
        return customerRepository.save(customer).toDTO()*/
        return Customer(null,"","","","", null).toDTO()
    }
}

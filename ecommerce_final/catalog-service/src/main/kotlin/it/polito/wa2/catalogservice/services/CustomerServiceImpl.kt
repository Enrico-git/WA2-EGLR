package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.domain.Customer
import it.polito.wa2.catalogservice.domain.User
import it.polito.wa2.catalogservice.dto.CustomerDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.dto.toDTO
import it.polito.wa2.catalogservice.exceptions.NotFoundException
import it.polito.wa2.catalogservice.exceptions.UnauthorizedException
import it.polito.wa2.catalogservice.repositories.CustomerRepository
import it.polito.wa2.catalogservice.repositories.UserRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.types.ObjectId
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
): CustomerService {

    override suspend fun addCustomer(customerDTO: CustomerDTO): CustomerDTO {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        if ( ! user.roles!!.contains("ADMIN") && user.id != customerDTO.userID )
            throw UnauthorizedException("Forbidden")

        val customer = Customer(
            name = customerDTO.name,
            surname = customerDTO.surname,
            address = customerDTO.address,
            email = customerDTO.email,
            user = customerDTO.userID
        )
        return customerRepository.save(customer).toDTO()
    }

    override suspend fun getCustomer(customerID: ObjectId): CustomerDTO {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        val customer = customerRepository.findById(customerID) ?: throw NotFoundException("Customer not found")
        if (! user.roles!!.contains("ADMIN") && customer.user != user.id)
            throw UnauthorizedException("Forbidden")
        return customer.toDTO()
    }

    override suspend fun updateCustomer(customerDTO: CustomerDTO, customerID: ObjectId): CustomerDTO {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        val customer = customerRepository.findById(customerID) ?: throw IllegalArgumentException("Customer not found")
        if (! user.roles!!.contains("ADMIN") && customer.user != user.id)
            throw UnauthorizedException("Forbidden")

        customer.address = customerDTO.address
        customer.name = customerDTO.name
        customer.surname = customerDTO.surname
        customer.email = customerDTO.email

        return customerRepository.save(customer).toDTO()
    }
}

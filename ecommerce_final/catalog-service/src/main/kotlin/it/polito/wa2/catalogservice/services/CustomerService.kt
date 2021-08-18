package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.CustomerDTO
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface CustomerService {
    @PreAuthorize("hasAuthority(\"CUSTOMER\") or hasAuthority(\"ADMIN\")")
    suspend fun addCustomer(customerDTO: CustomerDTO): CustomerDTO

    @PreAuthorize("hasAuthority(\"CUSTOMER\") or hasAuthority(\"ADMIN\")")
    suspend fun getCustomer(customerID: ObjectId): CustomerDTO

    @PreAuthorize("hasAuthority(\"CUSTOMER\") or hasAuthority(\"ADMIN\")")
    suspend fun updateCustomer(customerDTO: CustomerDTO, customerID: ObjectId): CustomerDTO
}
package it.polito.ecommerce.services

import it.polito.ecommerce.dto.CustomerDTO
import org.springframework.security.access.prepost.PreAuthorize

interface CustomerService {
    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isUser(authentication, #customerDTO.userID)")
    fun addCustomer(customerDTO: CustomerDTO): CustomerDTO

    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isCustomer(authentication, #customerID)")
    fun getCustomer(customerID: Long): CustomerDTO

    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isCustomer(authentication, #customerID)")
    fun updateCustomer(customerID: CustomerDTO, userID: Long): CustomerDTO
}
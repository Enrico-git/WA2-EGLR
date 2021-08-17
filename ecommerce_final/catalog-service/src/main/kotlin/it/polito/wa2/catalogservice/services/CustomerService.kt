package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.CustomerDTO
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

//TODO isUser and IsCustomer??
interface CustomerService {
    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isUser(authentication, #customerDTO.userID)")
    suspend fun addCustomer(customerDTO: CustomerDTO): CustomerDTO

    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isCustomer(authentication, #customerID)")
    suspend fun getCustomer(customerID: ObjectId): CustomerDTO

    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isCustomer(authentication, #customerID)")
    suspend fun updateCustomer(customerDTO: CustomerDTO, customerID: ObjectId): CustomerDTO
}
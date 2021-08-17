package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.CustomerDTO
import it.polito.wa2.catalogservice.services.CustomerService
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/customer")
class CustomerController(
    private val customerService: CustomerService
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createCustomer(@RequestBody @Validated customerDTO: CustomerDTO): CustomerDTO {
        return customerService.addCustomer(customerDTO)
    }

    @GetMapping("/{customerID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getCustomer(@PathVariable customerID: ObjectId): CustomerDTO {
        return customerService.getCustomer(customerID)
    }

    @PutMapping("/{customerID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateCustomer(
        @RequestBody @Validated customerDTO: CustomerDTO,
        @PathVariable customerID: ObjectId
    ): CustomerDTO {
        return customerService.updateCustomer(customerDTO, customerID)
    }

}
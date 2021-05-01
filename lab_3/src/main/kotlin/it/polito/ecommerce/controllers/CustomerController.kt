package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.services.CustomerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/customer")
@Validated
class CustomerController(
    private val customerService: CustomerService
) {
    @PostMapping("/")
    fun createCustomer(@RequestBody @Valid customerDTO: CustomerDTO) : ResponseEntity<CustomerDTO> {
        return ResponseEntity(customerService.addCustomer(customerDTO), HttpStatus.CREATED)
    }

    @GetMapping("/{customerID}")
    fun getCustomer(@PathVariable @Min(0) customerID: Long) : ResponseEntity<CustomerDTO> {
        return ResponseEntity(customerService.getCustomer(customerID), HttpStatus.OK)
    }

    @PutMapping("/{customerID}")
    fun updateCustomer(@RequestBody @Valid customerDTO: CustomerDTO, @PathVariable @Min(0) customerID: Long) : ResponseEntity<CustomerDTO> {
        return ResponseEntity(customerService.updateCustomer(customerDTO, customerID), HttpStatus.OK)
    }

}
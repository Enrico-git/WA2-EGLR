package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.services.WalletServiceImpl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(private val service: WalletServiceImpl) {
    @GetMapping("/{id}")
    fun asd(@PathVariable id: Int): WalletDTO? {
        return service.getWallet(id)
    }
}
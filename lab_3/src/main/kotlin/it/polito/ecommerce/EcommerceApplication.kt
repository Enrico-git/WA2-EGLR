package it.polito.ecommerce

import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.services.WalletServiceImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EcommerceApplication

fun main(args: Array<String>) {
    runApplication<EcommerceApplication>(*args)
}

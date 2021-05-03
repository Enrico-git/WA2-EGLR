package it.polito.wa2.webflux.domain

import org.springframework.data.annotation.Id
import java.math.BigDecimal

data class Product (
    @Id
    val id: Long?,
    val name: String,
    val price: BigDecimal,
    val producer_id: Long
)

data class Producer(
    @Id val id: Long?,
    val name: String,
    val address: Address)

data class Address(
    val street: String,
    val zip: String,
    val city: String
)


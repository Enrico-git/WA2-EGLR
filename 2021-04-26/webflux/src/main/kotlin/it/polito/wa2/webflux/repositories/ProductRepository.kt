package it.polito.wa2.webflux.repositories

import it.polito.wa2.webflux.domain.Product
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ProductRepository: ReactiveCrudRepository<Product, Long> {

    @Query("SELECT * FROM product WHERE name = :name")
    fun findByName(name:String): Flux<Product>

    fun findByNameStartingWith(prefix:String): Flux<Product>
}

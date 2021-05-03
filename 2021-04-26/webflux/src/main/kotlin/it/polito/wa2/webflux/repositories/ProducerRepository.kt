package it.polito.wa2.webflux.repositories

import it.polito.wa2.webflux.domain.Producer
import it.polito.wa2.webflux.domain.Product
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ProducerRepository: ReactiveCrudRepository<Producer, Long> {
    @Query("""
        SELECT 
            product.id as id, 
            product.name as name, 
            product.price as price, 
            producer.id as producer_id   
         FROM producer JOIN product 
         ON producer.id = product.producer_id
         WHERE producer.id = :producer_id
    """)
    fun findProducts(producer_id: Long): Flux<Product>

    @Query("""
        SELECT 
            product.id as id, 
            product.name as name, 
            product.price as price, 
            producer.id as producer_id   
         FROM producer JOIN product 
         ON producer.id = product.producer_id
         ORDER BY producer_id
    """)
    fun findProducts(): Flux<Product>




}

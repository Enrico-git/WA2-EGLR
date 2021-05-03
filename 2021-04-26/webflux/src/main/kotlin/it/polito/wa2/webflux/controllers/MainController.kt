package it.polito.wa2.webflux.controllers

import it.polito.wa2.webflux.domain.Producer
import it.polito.wa2.webflux.domain.Product
import it.polito.wa2.webflux.repositories.ProducerRepository
import it.polito.wa2.webflux.repositories.ProductRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class MainController(
    val productRepository: ProductRepository,
    val producerRepository: ProducerRepository
    ) {

    @GetMapping("products")
    fun products() : Flux<Product> {
        return productRepository.findAll()
    }

    @GetMapping("products/{prefix}")
    fun productsStartingWith(@PathVariable("prefix") prefix: String):
        Flux<Product> {
        return productRepository.findByNameStartingWith(prefix)
    }

    @GetMapping("count")
    fun countProducts(): Mono<Long> {
        return productRepository.count()
    }

    @GetMapping("count/{prefix}")
    fun countProductsStartingWith(
        @PathVariable("prefix") prefix:String): Mono<Long> {
        return productRepository.findByNameStartingWith(prefix).count()
    }

    @GetMapping("producers")
    fun producers(): Flux<Producer> {
        return producerRepository.findAll()
    }

    @GetMapping("producers/{id}")
    fun producer(@PathVariable("id") id : Long): Mono<Producer> {
        return producerRepository.findById(id)
    }

    @GetMapping("producers/{id}/products")
    fun productsByProducer(@PathVariable("id") id: Long): Flux<Product> {
        return producerRepository.findProducts(id)
    }

    @GetMapping("products/byProducers")
    fun productsByProducers(): Flux<List<Product>> {
        return producerRepository.findProducts()
            .bufferUntilChanged { it -> it.producer_id }
    }
}


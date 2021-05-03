package it.polito.wa2.webflux

import it.polito.wa2.webflux.domain.Address
import it.polito.wa2.webflux.domain.Producer
import it.polito.wa2.webflux.domain.Product
import it.polito.wa2.webflux.repositories.ProducerRepository
import it.polito.wa2.webflux.repositories.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux

@SpringBootApplication
class WebfluxApplication {

    fun randomName(len: Int): String {
        val random = java.util.Random()
        val vowels = listOf("a", "e", "i", "o", "u", "y")
        val consonants = listOf(
            "b", "c", "d", "f", "g", "h", "j", "k",
            "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "z"
        )
        val l = mutableListOf<String>()
        for (i in 0 until len) {
            if (i % 2 == 0)
                l.add(vowels[random.nextInt(vowels.size)])
            else
                l.add(consonants[random.nextInt(consonants.size)])
        }
        return l.joinToString("")
    }

    @Bean
    fun populateDB(
        productRepository: ProductRepository,
        producerRepository: ProducerRepository
    ): CommandLineRunner {
        return CommandLineRunner {
            val random = java.util.Random()
            val producers = Flux.range(1, 10)
                .map { Producer(null, "producer$it", Address("city$it", "zip$it", "city$it")) }
            val producerIds = producerRepository
                .saveAll(producers)
                .map { it.id!! }
                .collectList()
                .block()

            val products = Flux.range(1, 100)
                .map { Product(null,
                    randomName(10),
                    (it / 100.0).toBigDecimal(),
                    producerIds!![random.nextInt(producerIds.size)]!!
                ) }
                .doOnNext {
                    println(it)
                }

            productRepository
                .saveAll(products)
                .then(productRepository.count())
                .subscribe {
                    println("Count is $it")
                }

        }
    }
}

fun main(args: Array<String>) {
    runApplication<WebfluxApplication>(*args)
}

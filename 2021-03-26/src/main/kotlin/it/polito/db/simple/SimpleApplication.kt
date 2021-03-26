package it.polito.db.simple

import it.polito.db.simple.entities.Product
import it.polito.db.simple.repositories.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SimpleApplication{
    @Bean
    fun test(productRepository: ProductRepository): CommandLineRunner{
        println("Actual class implementing ProductRepository is ${productRepository.javaClass.canonicalName}")
        return CommandLineRunner {

            val p1 = Product(null, "Book", 12.5)
//            val p1 = Product()
//            //p1.id is default null
//            p1.name="Book"
//            p1.price=12.5
            productRepository.save(p1)
            println("After save p1.id = ${p1.id}")

            val p2 = Product(null, "Phone", 300.0)
//            val p2 = Product()
//            p2.name="Phone"
//            p2.price=300.0
            productRepository.save(p2)
//            productRepository.findAll().forEach{
//                println("Product(id: ${it.id}, name: ${it.name}, price: ${it.price})")
//            }
            productRepository.findByPriceBetween(100.0, 420.0).forEach{
                println("Product(id: ${it.id}, name: ${it.name}, price: ${it.price})")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<SimpleApplication>(*args)
}

package it.polito.wa2.orderservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.logging.Logger


@SpringBootApplication()
class OrderServiceApplication{
    @Bean
    fun getLogger(): Logger = Logger.getLogger("OrderServiceLogger")
}

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
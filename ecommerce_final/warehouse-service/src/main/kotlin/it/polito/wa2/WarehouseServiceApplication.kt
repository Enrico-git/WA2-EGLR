package it.polito.wa2.warehouse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class WarehouseServiceApplication

fun main(args: Array<String>) {
    runApplication<WarehouseServiceApplication>(*args)
    println("Warehouse service started")
}

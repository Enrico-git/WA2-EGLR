package it.polito.wa2.service1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class Service1Application

fun main(args: Array<String>) {
    runApplication<Service1Application>(*args)
}

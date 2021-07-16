package it.polito.wa2.service2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class Service2Application

fun main(args: Array<String>) {
    runApplication<Service2Application>(*args)
}

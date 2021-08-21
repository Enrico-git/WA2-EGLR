package it.polito.wa2.walletservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import java.util.logging.Logger


@SpringBootApplication
@EnableEurekaClient
class WalletServiceApplication{
    @Bean
    fun getLogger(): Logger = Logger.getLogger("it.polito.wa2.walletServiceLogger")
}

fun main(args: Array<String>) {
    runApplication<WalletServiceApplication>(*args)
}

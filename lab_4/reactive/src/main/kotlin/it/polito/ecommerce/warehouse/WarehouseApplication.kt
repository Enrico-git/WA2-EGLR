package it.polito.ecommerce.warehouse

import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableR2dbcRepositories
class WarehouseApplication{
    @Bean
    fun initializer (connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val cfi = ConnectionFactoryInitializer()
        cfi.setConnectionFactory(connectionFactory)
//        cfi.setDatabasePopulator(
//            ResourceDatabasePopulator(
//                ClassPathResource("schema.sql"),
//                //ClassPathResource("data.sql")
//                ClassPathResource("data11.sql")
//            )
//        )
        return cfi
    }
}

fun main(args: Array<String>) {
    runApplication<WarehouseApplication>(*args)
}

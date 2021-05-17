package it.polito.ecommerce.warehouse

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@SpringBootApplication
class WarehouseApplication {
    @Bean
    fun initializer (@Qualifier("connectionFactory") connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val cfi = ConnectionFactoryInitializer()
        cfi.setConnectionFactory(connectionFactory)
        cfi.setDatabasePopulator(
            ResourceDatabasePopulator(
                ClassPathResource("schema.sql"),
                ClassPathResource("data.sql")
            )
        )
        return cfi
    }
}

fun main(args: Array<String>) {
    runApplication<WarehouseApplication>(*args)
}

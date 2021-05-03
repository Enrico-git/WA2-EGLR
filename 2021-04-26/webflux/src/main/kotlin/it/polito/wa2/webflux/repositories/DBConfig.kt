package it.polito.wa2.webflux.repositories

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
@EnableR2dbcRepositories
class DBConfig: AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return ConnectionFactories.get(
            ConnectionFactoryOptions.builder().apply{
                option(ConnectionFactoryOptions.DRIVER, "pool")
                option(ConnectionFactoryOptions.PROTOCOL,"mariadb")
                option(ConnectionFactoryOptions.HOST,"localhost")
                option(ConnectionFactoryOptions.PORT, 3306)
                option(ConnectionFactoryOptions.USER, "wp2")
                //option(ConnectionFactoryOptions.PASSWORD, "root")
                option(ConnectionFactoryOptions.DATABASE, "reactive")
            }.build()
        )
        /*
        val conf = MariadbConnectionConfiguration
            .builder()
            .host("localhost")
            .port(3306)
            .username("root")
            .password("root")
            .database("reactive")
            .build()
        return MariadbConnectionFactory( conf )
            */
    }

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val cfi = ConnectionFactoryInitializer()
        cfi.setConnectionFactory(connectionFactory)
        cfi.setDatabasePopulator(
            ResourceDatabasePopulator(
                ClassPathResource("schema.sql")
            )
        )
        return cfi
    }

    override fun getCustomConverters(): MutableList<Any> =
        mutableListOf(ProducerReader(), ProducerWriter())

}

package it.polito.wa2.orderservice.config

import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

/**
 * Configuration used to connect to different databases using the same dbms
 * @param mongoClient the mongo client instance
 */
@Configuration
class MultipleMongoConnectionsConfig(private val mongoClient: MongoClient) {
    /**
     * Bean that returns the mongo template for the users database
     * @return the reactive mongo template initialized for users database
     */
    @Bean(name=["usersMongoTemplate"])
    fun reactiveMongoTemplate2(): ReactiveMongoTemplate? {
        return ReactiveMongoTemplate(mongoClient, "users")
    }

    /**
     * Bean that returns the mongo template for the orderservice database
     * @return the reactive mongo template initialized for orderservice database
     */
    @Bean(name=["ordersMongoTemplate"])
    fun reactiveMongoTemplate(): ReactiveMongoTemplate? {
        return ReactiveMongoTemplate(mongoClient, "orderservice")
    }
}

/**
 * Configuration used to link the order repository to the appropriate mongo template
 */
@Configuration
@EnableReactiveMongoRepositories(basePackages = ["it.polito.wa2.orderservice.repositories.orders"],
    reactiveMongoTemplateRef = "ordersMongoTemplate")
class OrderMongoConfig

/**
 * Configuration used to link the users repository to the appropriate mongo template
 */
@Configuration
@EnableReactiveMongoRepositories(basePackages = ["it.polito.wa2.orderservice.repositories.users"],
    reactiveMongoTemplateRef = "usersMongoTemplate")
class UserMongoConfig
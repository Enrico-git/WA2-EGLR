package it.polito.wa2.orderservice.config

import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionalOperator

@Configuration
@EnableTransactionManagement
class MongoConfig(
    @Value("\${spring.data.mongodb.database}") private val database: String,
    @Value("\${spring.data.mongodb.host}") private val host: String
) {

    @Bean
    fun reactiveMongoDatabaseFactory(): ReactiveMongoDatabaseFactory {
        return SimpleReactiveMongoDatabaseFactory(MongoClients.create("mongodb://$host"), database)
    }
    @Bean
    fun getTM(): ReactiveTransactionManager {
        return ReactiveMongoTransactionManager(reactiveMongoDatabaseFactory())
    }

//    Use this for transactions
    @Bean
    fun getOperator(): TransactionalOperator {
        return TransactionalOperator.create(getTM())
    }
}
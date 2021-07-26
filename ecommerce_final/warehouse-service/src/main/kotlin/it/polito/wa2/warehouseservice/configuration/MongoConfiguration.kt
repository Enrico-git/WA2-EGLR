package it.polito.wa2.warehouseservice.configuration

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoRepositories
class MongoConfiguration: AbstractReactiveMongoConfiguration() {
    @Bean
    fun mongoClient(): MongoClient{
        return MongoClients.create()
    }

    override fun getDatabaseName(): String {
        return "warehouseservice"
    }

    @Bean
    @Primary
    fun reactiveMongoTemplate(): ReactiveMongoTemplate{
        return ReactiveMongoTemplate(mongoClient(), "warehouseservice")
    }
}
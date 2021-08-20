package it.polito.wa2.catalogservice.configuration

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.SessionSynchronization
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.stereotype.Component
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionalOperator
import java.sql.Timestamp
import java.util.*

@Configuration
@Component
@EnableReactiveMongoRepositories(value = ["it.polito.wa2.catalogservice.repositories"])
@EnableTransactionManagement
class MongoConfiguration: AbstractReactiveMongoConfiguration() {
    @Value("\${spring.data.mongodb.database}") private val database: String = ""
    @Value("\${spring.data.mongodb.host}") private val host: String = ""


    override fun getDatabaseName() = database

    override fun customConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            listOf( // writing converter, reader converter
                TransactionReaderConverter(), TransactionWriterConverter()
            )
        )
    }

    @ReadingConverter
    class TransactionReaderConverter : Converter<Date, Timestamp> {
        override fun convert(source: Date): Timestamp {
            return Timestamp(source.time)
        }
    }

    @WritingConverter
    class TransactionWriterConverter : Converter<Timestamp, Date> {
        override fun convert(source: Timestamp): Date {
            return Date(source.time)
        }
    }

    /**
     * This is used to apply @Transactional in ReactiveMongo
     * https://spring.io/blog/2019/05/16/reactive-transactions-with-spring
     */
    @Bean
    fun getTM(): ReactiveTransactionManager {
        return ReactiveMongoTransactionManager(reactiveMongoDbFactory())
    }

    @Bean
    override fun reactiveMongoDbFactory(): ReactiveMongoDatabaseFactory {
        return SimpleReactiveMongoDatabaseFactory(MongoClients.create("mongodb://$host"), database)
    }

    @Bean
    fun getOperator(): TransactionalOperator {
        return TransactionalOperator.create(getTM())
    }
}
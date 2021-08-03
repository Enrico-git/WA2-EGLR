package it.polito.wa2.warehouseservice.configuration

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.SessionSynchronization
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
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
@EnableTransactionManagement
@EnableReactiveMongoRepositories(value = ["it.polito.wa2.warehouseservice.repositories"])
class MongoConfiguration: AbstractReactiveMongoConfiguration() {
    @Bean
    fun mongoClient(): MongoClient{
        return MongoClients.create()
    }

    override fun getDatabaseName(): String {
        return "warehouseservice"
    }

    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate{
        val template = ReactiveMongoTemplate(mongoClient(), "warehouseservice")
        template.setSessionSynchronization(SessionSynchronization.ALWAYS)
        return template
    }

    override fun customConversions(): MongoCustomConversions {
        return MongoCustomConversions(
                listOf(
                        TransactionReaderConverter(), TransactionWriterConverter()
                )
        )
    }

    @ReadingConverter
    class TransactionReaderConverter: Converter<Date, Timestamp> {
        override fun convert(source: Date): Timestamp{
            return Timestamp(source.time)
        }
    }

    @WritingConverter
    class TransactionWriterConverter: Converter<Timestamp, Date>{
        override fun convert(source: Timestamp): Date{
            return Date(source.time)
        }
    }

//    @Bean
//    @Primary
//    fun reactiveMongoDatabaseFactory(): ReactiveMongoDatabaseFactory {
//        return SimpleReactiveMongoDatabaseFactory(MongoClients.create("mongodb://$host"), database)
//    }

    @Bean
    fun getTM(): ReactiveTransactionManager{
        return ReactiveMongoTransactionManager(reactiveMongoTemplate().mongoDatabaseFactory)
    }

    @Bean
    fun getOperator(): TransactionalOperator {
        return TransactionalOperator.create(getTM())
    }
}
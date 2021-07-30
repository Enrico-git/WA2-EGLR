package it.polito.wa2.catalogservice.configuration

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.util.*

@Configuration
@Component
@EnableReactiveMongoRepositories(value = ["it.polito.wa2.catalogservice.repositories"])
class MongoConfiguration: AbstractReactiveMongoConfiguration() {
    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create()
    }

    override fun getDatabaseName(): String {
        return "catalogservice"
    }

    @Bean
    @Primary
    fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(mongoClient(), "catalogservice")
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
        override fun convert(source: Date): Timestamp {
            return Timestamp(source.time)
        }
    }

    @WritingConverter
    class TransactionWriterConverter: Converter<Timestamp, Date> {
        override fun convert(source: Timestamp): Date {
            return Date(source.time)
        }
    }
}
package it.polito.wa2.orderservice.config

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.stereotype.Component
import java.util.*

@Configuration
@EnableKafka
class KafkaConfig {
    private val bootstrapAddress: String? = "localhost:29092"

    @Bean
    fun getProducer(): KafkaProducer<String, String> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "5"
        props[ProducerConfig.RETRIES_CONFIG] = "2"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.CLIENT_ID_CONFIG] = "order_service_producer"
//        props[JsonDeserializer.TYPE_MAPPINGS] = "payment_done:com.example.order_serv.PaymentMsg"
//        props[ProducerConfig.ISOLATION_LEVEL_CONFIG] = "read_committed"
//        props[ProducerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        return KafkaProducer<String, String>(props)
    }

//    @Bean
//    fun createConsumer(): Consumer<String, PaymentMsg> {
//        val props = Properties()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
//        props[ConsumerConfig.GROUP_ID_CONFIG] = "group_id"
//        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
//            StringDeserializer::class.java
//        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
//            JsonDeserializer::class.java
//        props[JsonDeserializer.USE_TYPE_INFO_HEADERS] = false
//        props[JsonDeserializer.VALUE_DEFAULT_TYPE] = PaymentMsg::class.java
//        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
//        props[JsonDeserializer.TYPE_MAPPINGS] = "payment_done:com.example.order_serv.PaymentMsg"
////        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
//        return KafkaConsumer(props)
//    }
//
//    @Bean
//    fun createConsumer1(): Consumer<String,String> {
//        val props = Properties()
//        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
//        props[ConsumerConfig.GROUP_ID_CONFIG] = "group_id_2"
//        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
//            StringDeserializer::class.java
//        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
//            StringDeserializer::class.java
//        props[JsonDeserializer.USE_TYPE_INFO_HEADERS] = false
//        return KafkaConsumer(props)
//    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ConsumerConfig.GROUP_ID_CONFIG] = "order_service"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}
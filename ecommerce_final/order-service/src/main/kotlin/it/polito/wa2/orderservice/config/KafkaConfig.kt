package it.polito.wa2.orderservice.config

import it.polito.wa2.orderservice.dto.AbortProductReservationRequestDTO
import it.polito.wa2.orderservice.dto.PaymentRequestDTO
import it.polito.wa2.orderservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.orderservice.dto.ProductsReservationResponseDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
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
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.*

@Configuration
@EnableKafka
class KafkaConfig {
//    TODO change to docker addr
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
        return KafkaProducer<String, String>(props)
    }

    /**
     * Returns the kafka producer for PaymentRequest messages
     * This producer is used both for the PaymentRequest and the Abort Payment Request
     * as both messages have the same fields
     * @return the kafka producer with String as key and PaymentRequest class as value
     */
    @Bean
    fun getPaymentRequestProducer(): KafkaProducer<String, PaymentRequestDTO> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            JsonSerializer::class.java
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "5"
        props[ProducerConfig.RETRIES_CONFIG] = "2"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.CLIENT_ID_CONFIG] = "order_service_payment_req_producer"
//        props[JsonSerializer.TYPE_MAPPINGS] = "payment_done:it.polito.wa2.orderservice.domain.PaymentRequest"
//        props[ProducerConfig.ISOLATION_LEVEL_CONFIG] = "read_committed"
//        props[ProducerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false
        return KafkaProducer<String, PaymentRequestDTO>(props)
    }

    /**
     * Returns the kafka producer for ProductsReservationRequest messages
     * @return the kafka producer with String as key and ProductsReservationRequest class as value
     */
    @Bean
    fun getProductsReservationProducer(): KafkaProducer<String, ProductsReservationRequestDTO> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            JsonSerializer::class.java
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "5"
        props[ProducerConfig.RETRIES_CONFIG] = "2"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.CLIENT_ID_CONFIG] = "order_service_prod_reserv_req_producer"
        return KafkaProducer<String, ProductsReservationRequestDTO>(props)
    }

    /**
     * Returns the kafka producer for AbortProductReservationRequest messages
     * @return the kafka producer with String as key and AbortProductReservationRequest class as value
     */
    @Bean
    fun getAbortProductsReservationProducer(): KafkaProducer<String, AbortProductReservationRequestDTO> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            JsonSerializer::class.java
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "5"
        props[ProducerConfig.RETRIES_CONFIG] = "2"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.CLIENT_ID_CONFIG] = "order_service_abort_prod_reserv_req_producer"
        return KafkaProducer<String, AbortProductReservationRequestDTO>(props)
    }

    /**
     * Consumer factory for String message types
     * @return the consumer factory
     */
    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ConsumerConfig.GROUP_ID_CONFIG] = "order_service"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory(props)
    }

    /**
     * Make the kafka listener async
     * @return the concurrent listener container factory
     */
    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    /**
     * Consumer factory for ProductReservationDTO message types
     * @return the consumer factory
     */
    @Bean
    fun productsReservationConsumerFactory(): ConsumerFactory<String, ProductsReservationResponseDTO> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress!!
        props[ConsumerConfig.GROUP_ID_CONFIG] = "order_service"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[JsonDeserializer.TYPE_MAPPINGS] = "it.polito.wa2.warehouseservice.dto.ProductsReservationResponseDTO:it.polito.wa2.orderservice.dto.ProductsReservationResponseDTO"
        return DefaultKafkaConsumerFactory(props)
    }

    /**
     * Make the kafka listener async
     * @return the concurrent listener container factory
     */
    @Bean
    fun productsReservationListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ProductsReservationResponseDTO> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, ProductsReservationResponseDTO>()
        factory.consumerFactory = productsReservationConsumerFactory()
        return factory
    }
}
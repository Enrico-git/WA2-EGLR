package it.polito.wa2.warehouseservice.configuration

import it.polito.wa2.warehouseservice.dto.AbortProductReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
@EnableKafka
class KafkaConfiguration {

    @Value("\${spring.kafka.bootstrap-servers}")
    val bootstrapServers: String = ""

    @Bean
    fun reserveProductConsumerFactory(): ConsumerFactory<String, ProductsReservationRequestDTO>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG]  =  bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "warehouse_service"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        configProps[JsonDeserializer.TYPE_MAPPINGS] = "it.polito.wa2.orderservice.dto.ProductsReservationRequestDTO:it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO"
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun reserveProductContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ProductsReservationRequestDTO>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, ProductsReservationRequestDTO>()
        factory.consumerFactory = reserveProductConsumerFactory()
        return factory
    }

    @Bean
    fun abortProductsReservationConsumerFactory(): ConsumerFactory<String, AbortProductReservationRequestDTO>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG]  =  bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "warehouse_service"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        configProps[JsonDeserializer.TYPE_MAPPINGS] = "it.polito.wa2.orderservice.dto.AbortProductReservationRequestDTO:it.polito.wa2.warehouseservice.dto.AbortProductReservationRequestDTO"
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun abortProductsReservationContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, AbortProductReservationRequestDTO>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, AbortProductReservationRequestDTO>()
        factory.consumerFactory = abortProductsReservationConsumerFactory()
        return factory
    }

    @Bean
    fun mockReserveProductOkConsumerFactory(): ConsumerFactory<String, String>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG]  =  bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "warehouse_service"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory(configProps, StringDeserializer(), StringDeserializer())
    }

    @Bean
    fun mockReserveProductOkContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = mockReserveProductOkConsumerFactory()
        return factory
    }

    @Bean
    fun producerMockReserveProduct(): KafkaProducer<String, ProductsReservationRequestDTO>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        configProps[ProducerConfig.CLIENT_ID_CONFIG] = "mock_order_service_prod_reserv_req_producer"
        return KafkaProducer(configProps)
    }

//    @Bean
//    fun mockAbortReserveProductOkContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>{
//        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
//        factory.consumerFactory =
//    }

    @Bean
    fun producerMockAbortReserveProduct(): KafkaProducer<String, AbortProductReservationRequestDTO>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        configProps[ProducerConfig.CLIENT_ID_CONFIG] = "mock_order_service_abort_prod_reserv_req_producer"
        return KafkaProducer(configProps)
    }

    @Bean // (abort_)
    fun producerReserveProductFailed(): KafkaProducer<String, String>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        configProps[ProducerConfig.CLIENT_ID_CONFIG] = "warehouse_service_reserve_product_request_failed_producer"
        return KafkaProducer(configProps)
    }
}
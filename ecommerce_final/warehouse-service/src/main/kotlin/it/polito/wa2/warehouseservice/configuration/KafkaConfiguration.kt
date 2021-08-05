package it.polito.wa2.warehouseservice.configuration

import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
@EnableKafka
class KafkaConfiguration {

    @Value("\${spring.kafka.bootstrap-servers}")
    val bootstrapAddress: String = ""

    @Bean
    fun reserveProductConsumerFactory(): ConsumerFactory<String, ProductsReservationRequestDTO>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG]  =  bootstrapAddress
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "warehouse_service"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        configProps[JsonDeserializer.TYPE_MAPPINGS] = "it.polito.wa2.orderservice.dto.ProductsReservationRequestDTO:it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO"
        return DefaultKafkaConsumerFactory(configProps, StringDeserializer(), JsonDeserializer(ProductsReservationRequestDTO::class.java))
    }

    @Bean
    fun reserveProductContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, ProductsReservationRequestDTO>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, ProductsReservationRequestDTO>()
        factory.consumerFactory = reserveProductConsumerFactory()
        return factory
    }
}
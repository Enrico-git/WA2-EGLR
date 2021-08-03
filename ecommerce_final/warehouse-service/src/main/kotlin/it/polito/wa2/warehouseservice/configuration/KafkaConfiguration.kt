package it.polito.wa2.warehouseservice.configuration

import it.polito.wa2.warehouseservice.dto.KafkaReserveProductDTO
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
    fun reserveProductConsumerFactory(): ConsumerFactory<String, KafkaReserveProductDTO>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG]  =  bootstrapAddress
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "warehouse_service"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        return DefaultKafkaConsumerFactory(configProps, StringDeserializer(), JsonDeserializer(KafkaReserveProductDTO::class.java))
    }

    @Bean
    fun reserveProductContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, KafkaReserveProductDTO>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, KafkaReserveProductDTO>()
        factory.consumerFactory = reserveProductConsumerFactory()
        return factory
    }
}
package it.polito.wa2.walletservice.configuration

import it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
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
    fun paymentRequestConsumerFactory(): ConsumerFactory<String, KafkaPaymentRequestDTO>{ //payment_request
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "wallet_service"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        configProps[JsonDeserializer.TYPE_MAPPINGS] = "it.polito.wa2.orderservice.dto.PaymentRequestDTO:it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO"
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun paymentRequestContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, KafkaPaymentRequestDTO>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, KafkaPaymentRequestDTO>()
        factory.consumerFactory = paymentRequestConsumerFactory()
        return factory
    }

    @Bean
    fun producerPaymentRequestFailed(): KafkaProducer<String, String> {
        val configProps = mutableMapOf<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.CLIENT_ID_CONFIG] = "wallet_service_payment_req_failed_producer"
        return KafkaProducer(configProps)
    }
}

package it.polito.wa2.walletservice.configuration

import it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer


@Configuration
@EnableKafka
class KafkaConfiguration {

    @Value("\${spring.kafka.bootstrap-servers}")
    val bootstrapServers: String = ""

    // SAGA1: payment_request (new_order)

    /**
     * Consumer will read from "payment_request" partition: {orderID, amount, jwt}
     */
    @Bean
    fun paymentRequestConsumerFactory(): ConsumerFactory<String, KafkaPaymentRequestDTO>{ //payment_request
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "wallet_service"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        return DefaultKafkaConsumerFactory(configProps, StringDeserializer(), JsonDeserializer(KafkaPaymentRequestDTO::class.java)
        )
    }

    @Bean
    fun paymentRequestContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, KafkaPaymentRequestDTO>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, KafkaPaymentRequestDTO>()
        factory.consumerFactory = paymentRequestConsumerFactory()
        return factory
    }

    /**
     * Producer will put message {orderID} in "payment_request_failed" in case of error.
     * If the payment is fine, the insert in transactionRepository will trigger
     * debezium that will emit message in "payment_request_ok" partition.
     */
    @Bean
    fun producerFactory(): ProducerFactory<String, KafkaPaymentRequestDTO> { // payment_request_failed
        val configProps = mutableMapOf<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        configProps[ProducerConfig.CLIENT_ID_CONFIG] = "mock_order_service_payment_req_producer"
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, KafkaPaymentRequestDTO> {
        return KafkaTemplate(producerFactory())
    }

    //TODO SAGA2: abort_payment_request (delete_order)

}

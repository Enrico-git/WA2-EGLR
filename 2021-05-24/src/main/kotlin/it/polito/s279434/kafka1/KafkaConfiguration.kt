package it.polito.s279434.kafka1

import it.polito.s279434.kafka1.dto.RequestDTO
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.*
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaConfiguration {
    @Bean
    fun requestDTOProducerFactory(): ProducerFactory<String, RequestDTO>{
        val configProps = mutableMapOf<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun requestDTOKafkaTemplate(): KafkaTemplate<String, RequestDTO>{
        return KafkaTemplate(requestDTOProducerFactory())
    }

    @Bean
    fun requestDTOConsumerFactory(): ConsumerFactory<String, RequestDTO> {
        val configProps = mutableMapOf<String, Any>()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        configProps[JsonDeserializer.KEY_DEFAULT_TYPE] = RequestDTO::class.java
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    fun requestDTOContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, RequestDTO>{
        val factory = ConcurrentKafkaListenerContainerFactory<String, RequestDTO>()
        factory.consumerFactory = requestDTOConsumerFactory()
        return factory
    }

    @Bean
    fun topic1(): NewTopic{
        return TopicBuilder.name("Requests").build()
    }

    @Bean
    fun topic2(): NewTopic{
        return TopicBuilder.name("Responses").build()
    }

}
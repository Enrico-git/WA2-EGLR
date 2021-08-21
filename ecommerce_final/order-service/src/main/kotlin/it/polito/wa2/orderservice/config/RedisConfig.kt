package it.polito.wa2.orderservice.config

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.wa2.orderservice.domain.RedisStateMachine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.newSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
internal class RedisConfiguration {
    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory, objectMapper: ObjectMapper): ReactiveRedisTemplate<String, RedisStateMachine> {
        val valueSerializer = Jackson2JsonRedisSerializer(RedisStateMachine::class.java).apply {
            setObjectMapper(objectMapper)
        }

        return ReactiveRedisTemplate(
            factory,
            newSerializationContext<String, RedisStateMachine>(StringRedisSerializer())
                .value(valueSerializer)
                .build()
        )
    }
}

package it.polito.wa2.orderservice.repositories

import it.polito.wa2.orderservice.domain.RedisStateMachine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.executeAsFlow
import org.springframework.data.redis.core.incrementAndAwait
import org.springframework.data.redis.core.scanAsFlow
import org.springframework.stereotype.Repository

@Repository
class RedisStateMachineRepository(
    private val redisTemplate: ReactiveRedisTemplate<String, RedisStateMachine>
) {
    suspend fun get(key: String): RedisStateMachine? = redisTemplate.opsForValue().get(key).awaitFirstOrNull()
    suspend fun getAll(): Flow<RedisStateMachine> = redisTemplate.opsForSet().scanAsFlow("state_machines")
    suspend fun remove(stateMachine: RedisStateMachine): Long? = redisTemplate.opsForSet().remove("state_machines", stateMachine).awaitFirstOrNull()
    suspend fun add(stateMachine: RedisStateMachine): Long? = redisTemplate.opsForSet().add("state_machines", stateMachine).awaitFirstOrNull()
}
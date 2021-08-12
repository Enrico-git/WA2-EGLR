package it.polito.wa2.orderservice.domain

import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.dto.ProductDTO
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import org.springframework.data.redis.core.RedisHash
import java.math.BigDecimal

@RedisHash
data class RedisStateMachine(
    val initialState: StateMachineStates,
    var state: StateMachineStates?,
    val id: String = "",
    var failed: Boolean? = false,
    var completed: Boolean? = false,
    val customerEmail: String,
    val shippingAddress: String? = null,
    val amount: BigDecimal,
    val products: Set<ProductDTO>? = null,
    val auth: String
)

fun RedisStateMachine.toStateMachine(stateMachineBuilder: StateMachineBuilder) = stateMachineBuilder
        .id(id)
        .state(state)
        .customerEmail(customerEmail)
        .amount(amount)
        .products(products)
        .shippingAddress(shippingAddress)
        .auth(auth)
        .failed(failed)
        .completed(completed)
        .build()


fun StateMachineImpl.toRedisStateMachine() = RedisStateMachine(
    initialState,
    state,
    id,
    failed,
    completed,
    customerEmail,
    shippingAddress,
    amount,
    products,
    auth
)
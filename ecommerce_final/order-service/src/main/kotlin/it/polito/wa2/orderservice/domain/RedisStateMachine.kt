package it.polito.wa2.orderservice.domain

import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.dto.ProductDTO
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import it.polito.wa2.orderservice.statemachine.StateMachine
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEventPublisher
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
    val amount: BigDecimal,
    val products: Set<ProductDTO>? = null,
    val productsWarehouseLocation: Set<ProductLocation>? = null,
    val auth: String
)

fun RedisStateMachine.toStateMachine(stateMachineBuilder: StateMachineBuilder) = stateMachineBuilder
        .id(id)
        .state(state)
        .customerEmail(customerEmail)
        .amount(amount)
        .products(products)
        .productsWarehouseLocation(productsWarehouseLocation)
        .auth(auth)
        .failed(failed)
        .completed(completed)
        .build()


fun StateMachine.toRedisStateMachine() = RedisStateMachine(
    initialState,
    state,
    id,
    failed,
    completed,
    customerEmail,
    amount,
    products,
    productsWarehouseLocation,
    auth
)
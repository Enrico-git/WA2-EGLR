package it.polito.wa2.orderservice

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates
import it.polito.wa2.orderservice.repositories.RedisStateMachineRepository
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import it.polito.wa2.orderservice.statemachine.StateMachineBuilderImpl
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import kotlinx.coroutines.Job
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.util.ConcurrentReferenceHashMap
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger


@SpringBootApplication
class OrderServiceApplication{
    @Bean
    fun getLogger(): Logger = Logger.getLogger("OrderServiceLogger")

    @Bean(name=["new_order_sm"])
    fun getNewOrderStateMachine(applicationEventPublisher: ApplicationEventPublisher, redisStateMachineRepository: RedisStateMachineRepository): StateMachineBuilder{
        val builder = StateMachineBuilderImpl(applicationEventPublisher, redisStateMachineRepository)

        return builder
            .initialState(StateMachineStates.ORDER_REQ) // order creation request
            .finalState(StateMachineStates.ORDER_ISSUED) // order issued
            .source(StateMachineStates.ORDER_REQ)
            .target(StateMachineStates.PROD_AVAILABILITY_REQ)
            .event(StateMachineEvents.RESERVE_PRODUCTS)
            .and()
            .source(StateMachineStates.PROD_AVAILABILITY_REQ)
            .target(StateMachineStates.PROD_AVAILABILITY_OK)
            .event(StateMachineEvents.RESERVE_PRODUCTS_OK)
            .isPassive(true)
            .and()
            .source(StateMachineStates.PROD_AVAILABILITY_OK)
            .target(StateMachineStates.PAYMENT_REQ)
            .event(StateMachineEvents.PAYMENT_REQUEST)
            .and()
            .source(StateMachineStates.PAYMENT_REQ)
            .target(StateMachineStates.ORDER_ISSUED)
            .event(StateMachineEvents.PAYMENT_REQUEST_OK)
            .isPassive(true)
            .and()
//        rollback
            .source(StateMachineStates.PAYMENT_REQ)
            .target(StateMachineStates.PROD_AVAILABILITY_OK)
            .event(StateMachineEvents.PAYMENT_REQUEST_FAILED)
            .isRollingBack(true)
            .isPassive(true)
            .and()
            .source(StateMachineStates.PROD_AVAILABILITY_OK)
            .target(StateMachineStates.PROD_AVAILABILITY_REQ)
            .event(StateMachineEvents.ABORT_PRODUCTS_RESERVATION)
            .isRollingBack(true)
            .and()
            .source(StateMachineStates.PROD_AVAILABILITY_REQ)
            .target(StateMachineStates.ORDER_REQ)
            .event(StateMachineEvents.ABORT_PRODUCTS_RESERVATION_OK)
            .isRollingBack(true)
            .isPassive(true)
            .and()
            .source(StateMachineStates.PROD_AVAILABILITY_REQ)
            .target(StateMachineStates.ORDER_REQ)
            .event(StateMachineEvents.ABORT_PRODUCTS_RESERVATION_FAILED)
            .isRollingBack(true)
            .isPassive(true)
            .and()
            .source(StateMachineStates.PROD_AVAILABILITY_REQ)
            .target(StateMachineStates.ORDER_REQ)
            .event(StateMachineEvents.RESERVE_PRODUCTS_FAILED)
            .isRollingBack(true)
            .isPassive(true)
    }

    @Bean(name=["delete_order_sm"])
    fun getDeleteOrderStateMachine(applicationEventPublisher: ApplicationEventPublisher, redisStateMachineRepository: RedisStateMachineRepository): StateMachineBuilder{
        val builder = StateMachineBuilderImpl(applicationEventPublisher, redisStateMachineRepository)

        return builder
            .initialState(StateMachineStates.CANCEL_ORDER_REQ) // abort order req
            .finalState(StateMachineStates.ORDER_CANCELED) // order aborted successfully
            .source(StateMachineStates.CANCEL_ORDER_REQ)
            .target(StateMachineStates.ABORT_PAYMENT_REQ)
            .event(StateMachineEvents.ABORT_PAYMENT_REQUEST)
            .and()
            .source(StateMachineStates.ABORT_PAYMENT_REQ)
            .target(StateMachineStates.ABORT_PAYMENT_REQ_OK)
            .event(StateMachineEvents.ABORT_PAYMENT_REQUEST_OK)
            .isPassive(true)
            .and()
            .source(StateMachineStates.ABORT_PAYMENT_REQ_OK)
            .target(StateMachineStates.ABORT_PROD_RESERVATION_REQ)
            .event(StateMachineEvents.ABORT_PRODUCTS_RESERVATION)
            .and()
            .source(StateMachineStates.ABORT_PROD_RESERVATION_REQ)
            .target(StateMachineStates.ORDER_CANCELED)
            .event(StateMachineEvents.ABORT_PRODUCTS_RESERVATION_OK)
            .isPassive(true)
//        rollback
            .and()
            .source(StateMachineStates.ABORT_PAYMENT_REQ)
            .target(StateMachineStates.CANCEL_ORDER_REQ)
            .event(StateMachineEvents.ABORT_PAYMENT_REQUEST_FAILED)
            .isRollingBack(true)
            .isPassive(true)
            .and()
            .source(StateMachineStates.ABORT_PROD_RESERVATION_REQ)
            .target(StateMachineStates.ORDER_CANCELED)
            .event(StateMachineEvents.ABORT_PRODUCTS_RESERVATION_FAILED)
            .isRollingBack(true)
            .isPassive(true)
    }

    @Bean
    fun getJobsList(): ConcurrentReferenceHashMap<String, Job> = ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK)

    @Bean
    fun getSagasList(): ConcurrentHashMap<String, StateMachineImpl> = ConcurrentHashMap()

}

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
package it.polito.wa2.orderservice

import it.polito.wa2.orderservice.domain.OrderJob
import it.polito.wa2.orderservice.statemachine.StateMachine
import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import java.util.logging.Logger


@SpringBootApplication()
class OrderServiceApplication{
    @Bean
    fun getLogger(): Logger = Logger.getLogger("OrderServiceLogger")

    @Bean(name=["new_order_sm"])
    fun getNewOrderStateMachine(applicationEventPublisher: ApplicationEventPublisher): StateMachineBuilder{
        val builder = StateMachineBuilder(applicationEventPublisher)

        return builder
            .initialState("ORDER_REQ") // order creation request
            .finalState("ORDER_CREATED") // order issued
            .source("ORDER_REQ")
            .target("PROD_AVAILABILITY_REQ")
            .event("reserve_products")
            .and()
            .source("PROD_AVAILABILITY_REQ")
            .target("PROD_AVAILABILITY_OK")
            .event("reserve_products_ok")
            .and()
            .source("PROD_AVAILABILITY_OK")
            .target("PAYMENT_REQ")
            .event("payment_request")
            .and()
            .source("PAYMENT_REQ")
            .target("ORDER_CREATED")
            .event("payment_request_ok")
            .and()
//        rollback
            .source("PAYMENT_REQ")
            .target("PROD_AVAILABILITY_OK")
            .event("payment_request_failed")
            .and()
            .source("PROD_AVAILABILITY_OK")
            .target("PROD_AVAILABILITY_REQ")
            .event("abort_products_reservation")
            .and()
            .source("PROD_AVAILABILITY_REQ")
            .target("ORDER_REQ")
            .event("abort_products_reservation_ok")
            .and()
            .source("PROD_AVAILABILITY_REQ")
            .target("ORDER_REQ")
            .event("reserve_products_failed")
    }

    @Bean(name=["delete_order_sm"])
    fun getDeleteOrderStateMachine(applicationEventPublisher: ApplicationEventPublisher): StateMachineBuilder{
        val builder = StateMachineBuilder(applicationEventPublisher)

        return builder
            .initialState("CANCEL_ORDER_REQ") // abort order req
            .finalState("ORDER_CANCELED") // order aborted successfully
            .source("CANCEL_ORDER_REQ")
            .target("ABORT_PAYMENT_REQ")
            .event("abort_payment_request")
            .and()
            .source("ABORT_PAYMENT_REQ")
            .target("ABORT_PAYMENT_REQ_OK")
            .event("abort_payment_request_ok")
            .and()
            .source("ABORT_PAYMENT_REQ_OK")
            .target("ABORT_PROD_RESERVATION_REQ")
            .event("abort_products_reservation")
            .and()
            .source("ABORT_PROD_RESERVATION_REQ")
            .target("ORDER_CANCELED")
            .event("abort_products_reservation_ok")
//        rollback
            .and()
            .source("ABORT_PAYMENT_REQ")
            .target("CANCEL_ORDER_REQ")
            .event("abort_payment_request_failed")
    }

    @Bean
    fun getJobsList(): CopyOnWriteArrayList<OrderJob> = CopyOnWriteArrayList()

    @Bean
    fun getSagasList(): CopyOnWriteArrayList<StateMachine> = CopyOnWriteArrayList()

}

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
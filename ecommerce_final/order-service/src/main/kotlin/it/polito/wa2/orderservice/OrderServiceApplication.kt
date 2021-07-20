package it.polito.wa2.orderservice

import it.polito.wa2.orderservice.statemachine.StateMachineBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka
import java.util.logging.Logger


@SpringBootApplication()
class OrderServiceApplication{
    @Bean
    fun getLogger(): Logger = Logger.getLogger("OrderServiceLogger")

    @Bean(name=["new_order_sm"])
    fun getNewOrderStateMachine(applicationEventPublisher: ApplicationEventPublisher): StateMachineBuilder{
        val builder = StateMachineBuilder(applicationEventPublisher)

        return builder
            .initialState("STATE1") // ordine ricevuto
            .finalState("STATE5") //ordine pronto
            .source("STATE1")
            .target("STATE2")
            .event("reserve_products")
            .and()
            .source("STATE2")
            .target("STATE3")
            .event("reserve_products_ok")
            .and()
            .source("STATE3")
            .target("STATE4")
            .event("payment_request")
            .and()
            .source("STATE4")
            .target("STATE5")
            .event("payment_request_ok")
            .and()
//        rollback
            .source("STATE4")
            .target("STATE3")
            .event("payment_request_failed")
            .and()
            .source("STATE3")
            .target("STATE2")
            .event("abort_products_reservation")
            .and()
            .source("STATE2")
            .target("STATE1")
            .event("abort_products_reservation_ok")
            .and()
            .source("STATE2")
            .target("STATE1")
            .event("reserve_products_failed")
//        si uccide la macchina
    }

    @Bean(name=["delete_order_sm"])
    fun getDeleteOrderStateMachine(applicationEventPublisher: ApplicationEventPublisher): StateMachineBuilder{
        val builder = StateMachineBuilder(applicationEventPublisher)

        return builder
            .initialState("STATE1") // ordine ricevuto
            .finalState("STATE5") //ordine pronto
            .source("STATE1")
            .target("STATE2")
            .event("abort_payment_request")
            .and()
            .source("STATE2")
            .target("STATE3")
            .event("abort_payment_request_ok")
            .and()
            .source("STATE3")
            .target("STATE4")
            .event("abort_products_reservation")
            .and()
            .source("STATE4")
            .target("STATE5")
            .event("abort_products_reservation_ok")
//        rollback
            .and()
            .source("STATE2")
            .target("STATE1")
            .event("abort_payment_request_failed")
//        si uccide la macchina
    }

}

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
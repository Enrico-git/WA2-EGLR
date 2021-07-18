package it.polito.wa2.orderservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import com.mongodb.reactivestreams.client.MongoClients

import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.context.annotation.Bean

import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration

import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.boot.autoconfigure.mongo.MongoProperties

import org.springframework.data.mongodb.MongoDbFactory

import org.springframework.context.annotation.Primary

import org.springframework.data.mongodb.core.MongoTemplate

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.lang.Exception
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver

import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer


@SpringBootApplication()
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
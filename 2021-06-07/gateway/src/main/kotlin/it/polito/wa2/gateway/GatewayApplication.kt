package it.polito.wa2.gateway

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.timelimiter.TimeLimiterConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder
import org.springframework.cloud.client.circuitbreaker.Customizer
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableEurekaClient
@RestController
class GatewayApplication{


    @Bean
    fun defaultCustomizer(): Customizer<ReactiveResilience4JCircuitBreakerFactory> {
        return Customizer{ //Handling Error if Service is down
            factory -> factory.configureDefault {
                id -> Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                    .timeLimiterConfig(TimeLimiterConfig.ofDefaults())
                    .build()
            }
        }
    }

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder
            .routes()
            .route("svc1"){
                it -> it.path(true, "/service1/**")
                .filters{ f ->
                    f.circuitBreaker{ //Handling Error
                        it -> it.setFallbackUri("forward:/failure1")
                    }
                    f.rewritePath("/service1", "/")
                }
                .uri("lb://service1")
            }
            .route("svc2"){
                it -> it.path(true, "/service2/**")
                .filters{ f ->
                    f.circuitBreaker{ //Handling Error
                            it -> it.setFallbackUri("forward:/failure2")
                    }
                    f.rewritePath("/service2", "/")
                }
                .uri("lb://service2")
            }
            .build()
    }

    @GetMapping("/failure1")
    fun failure1(): String {
        return "Service1 is unavailable, try later"
    }

    @GetMapping("/failure2")
    fun failure2(): String {
        return "Service2 is unavailable, try later"
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)

    //localhost:8080/service1/
    //localhost:8080/service1/data
    //localhost:8080/service2/
}

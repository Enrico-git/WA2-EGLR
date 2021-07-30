package it.polito.wa2.catalogservice.configuration

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@LoadBalancerClient(name = "order-service", configuration = [OrderServiceLoadBalancingConfig::class])
class WebClientConfig {
    @LoadBalanced
    @Bean(name = ["order-service-client"])
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}
package it.polito.wa2.catalogservice.configuration

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
@LoadBalancerClient(name = "order-service", configuration = [OrderServiceLoadBalancingConfig::class])
class OrderServiceWebClientConfig {
    @LoadBalanced
    @Bean(name = ["order-service-client"])
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}

@Configuration
@LoadBalancerClient(name = "wallet-service", configuration = [WalletServiceLoadBalancingConfig::class])
class WalletServiceWebClientConfig {
    @LoadBalanced
    @Bean(name = ["wallet-service-client"])
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}

@Configuration
@LoadBalancerClient(name = "warehouse-service", configuration = [WarehouseServiceLoadBalancingConfig::class])
class WebClientConfig {
    @LoadBalanced
    @Bean(name = ["warehouse-service-client"])
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}
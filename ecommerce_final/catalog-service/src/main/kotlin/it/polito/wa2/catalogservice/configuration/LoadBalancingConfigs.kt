package it.polito.wa2.catalogservice.configuration

import com.netflix.discovery.EurekaClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.client.DefaultServiceInstance
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import reactor.core.publisher.Flux

@Configuration
class OrderServiceLoadBalancingConfig(@Qualifier("eurekaClient") private val eurekaClient: EurekaClient) {
    @Bean
    @Primary
    fun getOrderServiceInstanceListSupplier() : ServiceInstanceListSupplier{
        return ServiceInstanceListSupplier("order-service", eurekaClient)
    }
}

@Configuration
class WalletServiceLoadBalancingConfig(@Qualifier("eurekaClient") private val eurekaClient: EurekaClient) {
    @Bean
    @Primary
    fun getWalletServiceInstanceListSupplier() : ServiceInstanceListSupplier{
        return ServiceInstanceListSupplier("wallet-service", eurekaClient)
    }
}

@Configuration
class WarehouseServiceLoadBalancingConfig(@Qualifier("eurekaClient") private val eurekaClient: EurekaClient) {
    @Bean
    @Primary
    fun getWarehouseServiceInstanceListSupplier() : ServiceInstanceListSupplier{
        return ServiceInstanceListSupplier("warehouse-service", eurekaClient)
    }
}

class ServiceInstanceListSupplier(private val serviceID: String, private val eurekaClient: EurekaClient) : ServiceInstanceListSupplier{
    override fun get(): Flux<List<ServiceInstance>> {
        return Flux.just(eurekaClient
            .getApplication(serviceID)
            .instances
            .map { DefaultServiceInstance(it.instanceId, it.appName, it.hostName, it.port, false) })
    }
    override fun getServiceId(): String = serviceID
}

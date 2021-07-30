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
    fun getServiceInstanceListSupplier() : ServiceInstanceListSupplier{
        return OrderServiceInstanceListSupplier("order-service", eurekaClient)
    }
}

class OrderServiceInstanceListSupplier(private val serviceID: String, private val eurekaClient: EurekaClient) : ServiceInstanceListSupplier{
    override fun get(): Flux<List<ServiceInstance>> {
    return Flux.just(eurekaClient
                .getApplication(serviceID)
                .instances
                .map { DefaultServiceInstance(it.instanceId, it.appName, it.hostName, it.port, false) })
    }

    override fun getServiceId(): String = serviceID

}
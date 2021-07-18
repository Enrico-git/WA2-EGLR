package it.polito.wa2.orderservice.common

import org.springframework.context.annotation.Configuration
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

/**
 * Configuration to allow the use of pagination in the requests
 */
@Configuration
class PageableWebFluxConfiguration : WebFluxConfigurer {
    /**
     * Adds the pagination custom resolver
     * @param configurer used to add the pageable configurer to the list
     */
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(ReactivePageableHandlerMethodArgumentResolver())
    }
}
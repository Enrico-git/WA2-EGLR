package it.polito.wa2.warehouseservice.configuration

import it.polito.wa2.warehouseservice.security.AuthenticationManager
import it.polito.wa2.warehouseservice.security.SecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


/**
 * Entrypoint for security. it sets authenticationManager and securityContext
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(order = 100)
class WebSecurityConfig(
        private val authenticationManager: AuthenticationManager,
        private val securityContextRepository: SecurityContextRepository
){
    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val patterns = arrayOf("/auth/**")
        return http
                .exceptionHandling()
                .authenticationEntryPoint { swe: ServerWebExchange, e: AuthenticationException? ->
                    Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
                }.accessDeniedHandler { swe: ServerWebExchange, e: AccessDeniedException? ->
                    Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
                }.and()
                //.cors().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.GET,"/products/**/warehouses", ).authenticated()
                .pathMatchers(HttpMethod.GET,"/products/**", ).permitAll()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .anyExchange().authenticated()
                .and()
                .build()
    }

//    @Bean
//    fun passwordEncoder(): BCryptPasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
}

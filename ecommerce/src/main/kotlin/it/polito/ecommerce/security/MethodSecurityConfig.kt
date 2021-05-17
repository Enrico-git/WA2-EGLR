package it.polito.ecommerce.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class MethodSecurityConfig(
    private val expressionHandler: CustomMethodSecurityExpressionHandler
) : GlobalMethodSecurityConfiguration() {
    override fun createExpressionHandler(): MethodSecurityExpressionHandler {
        return expressionHandler
    }
}
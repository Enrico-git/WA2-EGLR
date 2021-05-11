package it.polito.ecommerce.security

import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.repositories.WalletRepository
import org.aopalliance.intercept.MethodInvocation
import org.apache.naming.factory.BeanFactory
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class CustomMethodSecurityExpressionHandler(
    private val beanFactory: AbstractAutowireCapableBeanFactory,
) : DefaultMethodSecurityExpressionHandler() {
    private val trustResolver: AuthenticationTrustResolverImpl = AuthenticationTrustResolverImpl()
    override fun createSecurityExpressionRoot(
        authentication: Authentication?,
        invocation: MethodInvocation?
    ): MethodSecurityExpressionOperations {
        val root = beanFactory.getBean(CustomMethodSecurityExpressionRoot::class.java, authentication!!)
        root.setPermissionEvaluator(permissionEvaluator)
        root.setTrustResolver(this.trustResolver)
        root.setRoleHierarchy(roleHierarchy)
        return root
    }
}
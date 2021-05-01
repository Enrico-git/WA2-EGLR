package it.polito.ecommerce.security

import it.polito.ecommerce.dto.UserDetailsDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.UserRepository
import it.polito.ecommerce.repositories.WalletRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Scope
import org.springframework.security.access.expression.SecurityExpressionRoot
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

//@Component
//@Scope(value = "prototype")
//class CustomMethodSecurityExpressionRoot(@Lazy authentication: Authentication) : SecurityExpressionRoot(authentication), MethodSecurityExpressionOperations {
//
//
//    @Autowired
//    private lateinit var walletRepository: WalletRepository
//
//    @Autowired
//    private lateinit var customerRepository: CustomerRepository


class CustomMethodSecurityExpressionRoot(authentication: Authentication,
    private val walletRepository: WalletRepository,
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository)
    : SecurityExpressionRoot(authentication), MethodSecurityExpressionOperations {


    fun isOwner(authentication: Authentication, walletID: Long): Boolean {
        val principal = authentication.principal as UserDetailsDTO
        val walletOpt = walletRepository.getWalletByUserAndId(principal.username, walletID)
        return walletOpt.isPresent
    }

    fun isCustomer(authentication: Authentication, customerID: Long): Boolean {
        val principal = authentication.principal as UserDetailsDTO
        val customerOpt = customerRepository.findByUserAndID(principal.username, customerID)
        return customerOpt.isPresent
    }

    fun isUser(authentication: Authentication, userID: Long): Boolean {
        val principal = authentication.principal as UserDetailsDTO
        return userRepository.findByUsername(principal.username).get().getId() == userID
    }

    override fun setFilterObject(filterObject: Any?) {
        this.filterObject = filterObject!!
    }

    override fun getFilterObject(): Any {
        return this.filterObject
    }

    override fun setReturnObject(returnObject: Any?) {
        this.returnObject = returnObject!!
    }

    override fun getReturnObject(): Any {
        return returnObject
    }

    override fun getThis(): Any {
        return this
    }


}

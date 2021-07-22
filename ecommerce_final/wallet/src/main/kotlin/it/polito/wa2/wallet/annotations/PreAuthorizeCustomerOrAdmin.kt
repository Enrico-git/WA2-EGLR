package it.polito.wa2.wallet.annotations

import org.springframework.security.access.prepost.PreAuthorize

@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority(\"CUSTOMER\")" +
        "|| hasAuthority(\"ADMIN\")" )
annotation class PreAuthorizeCustomerOrAdmin {
}

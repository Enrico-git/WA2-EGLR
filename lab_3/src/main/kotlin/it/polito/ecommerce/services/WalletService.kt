package it.polito.ecommerce.services
import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface WalletService {
    @PreAuthorize("hasAuthority(\"CUSTOMER\")")
//    @PreAuthorize("hasAuthority(\"CUSTOMER\") and authorization.principal.customer.wallet == walletID")
//    TODO check hasPermission
//    https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html
    fun getWallet(walletID: Long): WalletDTO
    @PreAuthorize("hasAuthority(\"CUSTOMER\")")
    fun addWallet(customerDTO: CustomerDTO): WalletDTO
    @PreAuthorize("hasAuthority(\"CUSTOMER\")")
    fun performTransaction(transactionDTO: TransactionDTO): TransactionDTO
    @PreAuthorize("hasAuthority(\"CUSTOMER\")")
    fun getWalletTransactions(walletID: Long, from: Long? = null, to: Long? = null, pageable: Pageable): List<TransactionDTO>
    @PreAuthorize("hasAuthority(\"CUSTOMER\")")
    fun getWalletSingleTransaction(walletID: Long, transactionID: Long): TransactionDTO
}
package it.polito.ecommerce.services
import it.polito.ecommerce.dto.CreateWalletDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface WalletService {
    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isOwner(authentication, #walletID)")
    fun getWallet(walletID: Long): WalletDTO
    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isCustomer(authentication, #createWalletDTO.id)")
    fun addWallet(createWalletDTO: CreateWalletDTO): WalletDTO
    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isOwner(authentication, #transactionDTO.senderID)")
    fun performTransaction(transactionDTO: TransactionDTO): TransactionDTO
    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isOwner(authentication, #walletID)")
    fun getWalletTransactions(walletID: Long, from: Long? = null, to: Long? = null, pageable: Pageable): List<TransactionDTO>
    @PreAuthorize("hasAuthority(\"CUSTOMER\") and isOwner(authentication, #walletID)")
    fun getWalletSingleTransaction(walletID: Long, transactionID: Long): TransactionDTO
}

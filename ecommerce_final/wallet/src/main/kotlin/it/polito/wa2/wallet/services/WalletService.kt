package it.polito.wa2.wallet.services

import it.polito.wa2.wallet.annotations.PreAuthorizeCustomerOrAdmin
import it.polito.wa2.wallet.dto.TransactionDTO
import it.polito.wa2.wallet.dto.WalletDTO
import it.polito.wa2.wallet.entities.Wallet
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface WalletService {
    @PreAuthorizeCustomerOrAdmin
    suspend fun getWallet(walletID: String): WalletDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun createWallet(wallet: Wallet): WalletDTO

    @PreAuthorizeCustomerOrAdmin
    suspend fun createTransaction(walletID: String, transactionDTO: TransactionDTO): TransactionDTO

    @PreAuthorizeCustomerOrAdmin
    suspend fun getAllTransactions(walletID: String, from: Long?, to: Long?, pageable: Pageable): Flow<TransactionDTO>
}

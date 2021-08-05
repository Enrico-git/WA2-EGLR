package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.TransactionDTO
import it.polito.wa2.catalogservice.dto.WalletDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize

//TODO which filter on newTransaction?
interface WalletService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getWallet(walletID: String): WalletDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getTransactions(walletID: Long, from: Long?, to: Long?, page: Int?, size: Int?): Flow<TransactionDTO>
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getTransaction(walletID: String, transactionID: String): TransactionDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun newWallet(walletDTO: WalletDTO): WalletDTO
    suspend fun newTransaction(walletID: String, transactionDTO: TransactionDTO): TransactionDTO
}
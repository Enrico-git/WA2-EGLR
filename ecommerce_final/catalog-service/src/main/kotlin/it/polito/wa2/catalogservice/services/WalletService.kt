package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.TransactionDTO
import it.polito.wa2.catalogservice.dto.WalletDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface WalletService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getWallet(walletID: ObjectId): WalletDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getTransactions(walletID: ObjectId, from: Long?, to: Long?, page: Int?, size: Int?): Flow<TransactionDTO>
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getTransaction(walletID: ObjectId, transactionID: ObjectId): TransactionDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun newWallet(walletDTO: WalletDTO): WalletDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun newTransaction(walletID: ObjectId, transactionDTO: TransactionDTO): TransactionDTO
}
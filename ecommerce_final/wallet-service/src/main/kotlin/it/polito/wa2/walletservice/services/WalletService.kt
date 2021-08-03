package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.annotations.PreAuthorizeCustomerOrAdmin
import it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO
import it.polito.wa2.walletservice.dto.TransactionDTO
import it.polito.wa2.walletservice.dto.WalletDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface WalletService {
    @PreAuthorizeCustomerOrAdmin
    suspend fun getWallet(walletID: String): WalletDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun createWallet(walletDTO: WalletDTO): WalletDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")") // recharge wallet
    suspend fun createRechargeTransaction(walletID: String, transactionDTO: TransactionDTO): TransactionDTO

    suspend fun createPaymentOrRefundTransaction (topic: String, paymentRequestDTO: KafkaPaymentRequestDTO): Boolean? //Kafka

    @PreAuthorizeCustomerOrAdmin
    suspend fun getAllTransactions(walletID: String, from: Long?, to: Long?, pageable: Pageable): Flow<TransactionDTO>

    @PreAuthorizeCustomerOrAdmin
    suspend fun getTransaction(walletID: String, transactionID: String): TransactionDTO

    suspend fun mockPaymentOrAbortRequest(): String

}

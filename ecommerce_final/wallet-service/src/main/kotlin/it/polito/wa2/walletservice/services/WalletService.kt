package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.annotations.PreAuthorizeCustomerOrAdmin
import it.polito.wa2.walletservice.dto.KafkaPaymentOrRefundRequestDTO
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

    @PreAuthorizeCustomerOrAdmin
    suspend fun getAllTransactions(walletID: String, from: Long?, to: Long?, pageable: Pageable): Flow<TransactionDTO>

    @PreAuthorizeCustomerOrAdmin
    suspend fun getTransaction(walletID: String, transactionID: String): TransactionDTO

    suspend fun createPaymentTransaction(paymentRequestDTO: KafkaPaymentOrRefundRequestDTO): Boolean? //Kafka

    suspend fun createRefundTransaction(abortPaymentRequestDTO: KafkaPaymentOrRefundRequestDTO): Boolean?

}

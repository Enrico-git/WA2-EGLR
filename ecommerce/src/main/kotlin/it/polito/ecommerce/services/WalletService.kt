package it.polito.ecommerce.services

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import org.springframework.data.domain.Pageable

interface WalletService {
    fun addWallet(customerDTO: CustomerDTO): WalletDTO
    fun getWallet(walletID: Long): WalletDTO
    fun performTransaction(transactionDTO: TransactionDTO): TransactionDTO
    fun getWalletTransactions(walletID: Long, from: Long? = null,
                              to: Long? = null,
                              pageable: Pageable ) : List<TransactionDTO>

    fun getWalletTransaction(walletID: Long, transactionID: Long): TransactionDTO

}
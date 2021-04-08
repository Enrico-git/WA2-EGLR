package it.polito.ecommerce.services

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO

interface WalletService {
    fun getWallet(walletID: Long): WalletDTO
    fun addWallet(customerDTO: CustomerDTO): WalletDTO
    fun performTransaction(transactionDTO: TransactionDTO): TransactionDTO
    fun getWalletTransactions(walletID: Long, from: Long? = null, to: Long? = null): List<TransactionDTO>
    fun getWalletTransaction(walletID: Long, transactionID: Long): TransactionDTO
}
package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Customer
import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import java.sql.Timestamp

interface WalletService {
    fun addWallet(customerID: Int): WalletDTO
    fun getWallet(walletID: Int): WalletDTO
    fun performTransaction(senderID: Int, receiverID: Int, amount: Double): TransactionDTO
//    fun getWalletTransactions(walletID: Int): List<TransactionDTO>
    fun getWalletTransactions(walletID: Int,
                                         from: Long? = null,
                                         to: Long? = null)
    : List<TransactionDTO>
}
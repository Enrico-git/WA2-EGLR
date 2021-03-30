package it.polito.ecommerce.services

import it.polito.ecommerce.dto.WalletDTO

interface WalletService {
//    fun addWallet(customerID: Integer): Boolean
    fun getWallet(walletID: Int): WalletDTO?
//    fun performTransaction(sender: Integer, receiver: Integer, amount: Double): Boolean
//    fun getWalletTransactions(walletID: Integer,
//                              from: Timestamp? = null,
//                              to: Timestamp? = null)
//    : List<TransactionDTO>
}
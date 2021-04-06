package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Customer
import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import java.math.BigDecimal
import java.sql.Timestamp

interface WalletService {
    fun addWallet(customerID: Long): WalletDTO
    fun getWallet(walletID: Long): WalletDTO
    fun performTransaction(senderID: Long, receiverID: Long, amount: BigDecimal): TransactionDTO
//    fun getWalletTransactions(walletID: Long): List<TransactionDTO>
    fun getWalletTransactions(walletID: Long,
                                         from: Long? = null,
                                         to: Long? = null)
    : List<TransactionDTO>
}
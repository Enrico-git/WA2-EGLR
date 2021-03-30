package it.polito.ecommerce.services

import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import org.springframework.stereotype.Service
import java.sql.Timestamp

@Service
interface WalletService {
    fun addWallet(customerID: Int): Boolean
    fun getWallet(walletID: Int): WalletDTO?
    fun performTransaction(source: Int, dest: Int, amount: Double): Boolean
    fun getWalletTransactions(walletID: Int, from: Timestamp? = null,
                              to: Timestamp? = null) :List<TransactionDTO>

}
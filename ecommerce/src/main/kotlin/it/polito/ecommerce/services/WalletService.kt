package it.polito.ecommerce.services

import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Timestamp

@Service
interface WalletService {
    fun addWallet(customerID: Int): WalletDTO
    fun getWallet(walletID: Int): WalletDTO
    fun performTransaction(senderID: Int, receiverID: Int, amount: BigDecimal): TransactionDTO
    fun getWalletTransactions(walletID: Int, from: Long? = null,
                              to: Long? = null) :List<TransactionDTO>

}
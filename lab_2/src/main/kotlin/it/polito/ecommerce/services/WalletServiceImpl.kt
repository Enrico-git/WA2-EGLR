package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.TransactionRepository
import it.polito.ecommerce.repositories.WalletRepository
import javassist.NotFoundException
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.sql.Timestamp
import java.time.Instant
import javax.transaction.Transactional
import kotlin.NoSuchElementException

@Service
@Transactional
class WalletServiceImpl(private val walletRepository: WalletRepository,
private val customerRepository: CustomerRepository,
private val transactionRepository: TransactionRepository): WalletService {
    override fun getWallet(walletID: Int): WalletDTO? {
        val walletOpt = walletRepository.findById(walletID)
        if ( ! walletOpt.isPresent)
            throw NotFoundException("Not found")
//        val wallet = if(walletOpt.isPresent) walletOpt.get() else null
//        if(wallet != null)
        val wallet = walletOpt.get()
        return WalletDTO(wallet.id!!, wallet.balance, wallet.customer?.id!!)
//        else
//            return null
    }

    override fun addWallet(customerID: Int): WalletDTO {
        val customer = customerRepository.findById(customerID)
        if ( ! customer.isPresent)
            throw NotFoundException("Customer not found")
        println(customer.get().name)
        val wallet = Wallet(
            id = null,
            customer = customer.get()
        )
        return walletRepository.save(wallet).toDTO()
    }

    override fun performTransaction(senderID: Int, receiverID: Int, amount: Double): TransactionDTO {
        if (senderID == receiverID)
            throw Exception("You can't send money to yourself")
        val wallets = walletRepository.findAllById(listOf<Int>(senderID, receiverID))
        var senderWallet: Wallet
        var receiverWallet: Wallet
        try {
            senderWallet = wallets.first{it.id == senderID}
            receiverWallet = wallets.first{it.id == receiverID}
        } catch (e: NoSuchElementException) {
            throw NotFoundException("One of the wallets was not found")
        }

        if ( ! checkBalance(senderWallet, amount))
            throw Exception("Balance not high enough")

        val transaction = Transaction(
            id = null,
            timestamp = Timestamp(Instant.now().getEpochSecond() * 1000),
            sender = senderWallet,
            receiver = receiverWallet,
            amount = amount
        )
        if (! transferMoney(senderWallet, receiverWallet, amount))
            throw Exception("Could not transfer money")

        return transactionRepository.save(transaction).toDTO()

    }

    override fun getWalletTransactions(walletID: Int, from: String?, to: String?): List<TransactionDTO> {
        val wallet = walletRepository.findById(walletID)
        if ( ! wallet.isPresent)
            throw NotFoundException("Could not fetch wallet")
        if (from != null && to != null) {
            return transactionRepository.findAllByWalletAndByTimestampBetween(wallet.get(), Timestamp.valueOf(from.replace("T", " ")), Timestamp.valueOf(to.replace("T", " "))).map { it.toDTO() }
        }
        return transactionRepository.findAllByWallet(wallet.get()).map{it.toDTO()}
    }

    fun checkBalance(wallet: Wallet, amount: Double): Boolean{
        return wallet.balance >= amount && amount > 0
    }

    fun transferMoney(senderWallet: Wallet, receiverWallet: Wallet, amount: Double): Boolean{
        try {
            senderWallet.balance -= amount
            walletRepository.save(senderWallet)
            receiverWallet.balance += amount
            walletRepository.save(receiverWallet)
        } catch (e: IllegalArgumentException) {
            println(e)
            return false
        }
        return true
    }
}
package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.TransactionRepository
import it.polito.ecommerce.repositories.WalletRepository
import javassist.NotFoundException
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.transaction.Transactional
import kotlin.NoSuchElementException
import kotlin.jvm.Throws

// TODO DB CRASH
@Service
@Transactional
class WalletServiceImpl(private val walletRepository: WalletRepository,
private val customerRepository: CustomerRepository,
private val transactionRepository: TransactionRepository): WalletService{
//    Check if we have to use @Throws
    override fun getWallet(walletID: Long): WalletDTO {
        val walletOpt = walletRepository.findById(walletID)
        if ( ! walletOpt.isPresent)
            throw NotFoundException("Not found")
        val wallet = walletOpt.get()
        return wallet.toDTO()
    }

    override fun addWallet(customerID: Long): WalletDTO {
        val customerOpt = customerRepository.findById(customerID)
        if (  ! customerOpt.isPresent )
            throw NotFoundException("Customer not found")
        val wallet = Wallet(
            id = null,
            customer = customerOpt.get()
        )
        return walletRepository.save(wallet).toDTO()
    }

    override fun performTransaction(senderID: Long, receiverID: Long, amount: BigDecimal): TransactionDTO {
        if (senderID == receiverID)
            throw IllegalArgumentException("You can't send money to yourself")
        val wallets = walletRepository.findAllById(listOf<Long>(senderID, receiverID))
        val senderWallet: Wallet? = wallets.find{it.id == senderID}
        val receiverWallet: Wallet? = wallets.find{it.id == receiverID}
        if (senderWallet == null || receiverWallet == null)
            throw IllegalArgumentException("One of the wallets doesn't exist")

        if ( isBalanceInsufficient(senderWallet, amount))
            throw IllegalArgumentException("Balance not high enough")

        transferMoney(senderWallet, receiverWallet, amount)

        val transaction = Transaction(
            id = null,
            timestamp = Timestamp(System.currentTimeMillis()),
            sender = senderWallet,
            receiver = receiverWallet,
            amount = amount
        )

        return transactionRepository.save(transaction).toDTO()

    }

    override fun getWalletTransactions(walletID: Long, from: Long?, to: Long?): List<TransactionDTO> {
        //        TODO Pagination

        val walletOpt = walletRepository.findById(walletID)
        if ( ! walletOpt.isPresent)
            throw NotFoundException("Could not fetch wallet")


        if ( from != null && to != null) {
            return transactionRepository
                    .findAllByWalletAndByTimestampBetween(walletOpt.get(), Timestamp(from), Timestamp(to))
                    .map{it.toDTO()}
        }
        if ( from != null || to != null)
            throw IllegalArgumentException("Invalid parameters")

        return transactionRepository.findAllByWallet(walletOpt.get()).map{it.toDTO()}

//        Using JPA smart queries

//        return transactionRepository.findAllBySenderOrReceiver(wallet, wallet).map{it.toDTO()}
    }

    fun isBalanceInsufficient(wallet: Wallet, amount: BigDecimal): Boolean{
        return wallet.balance < amount
    }

    fun transferMoney(senderWallet: Wallet, receiverWallet: Wallet, amount: BigDecimal): Unit {
        senderWallet.balance -= amount
        walletRepository.save(senderWallet)
        receiverWallet.balance += amount
        walletRepository.save(receiverWallet)
    }
}
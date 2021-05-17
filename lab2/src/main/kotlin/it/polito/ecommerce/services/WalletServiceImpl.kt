package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.toDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.TransactionRepository
import it.polito.ecommerce.repositories.WalletRepository
import javassist.NotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Timestamp
import javax.transaction.Transactional

@Service
@Transactional
class WalletServiceImpl(
    private val walletRepository: WalletRepository,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : WalletService {
    override fun getWallet(walletID: Long): WalletDTO {
        val walletOpt = walletRepository.findById(walletID)
        if (!walletOpt.isPresent)
            throw NotFoundException("Wallet not found")
        val wallet = walletOpt.get()
        return wallet.toDTO()
    }

    override fun addWallet(customerDTO: CustomerDTO): WalletDTO {
        val customer = customerRepository.findById(customerDTO.id!!)
        if (!customer.isPresent)
            throw IllegalArgumentException("Customer does not exist")
        val wallet = Wallet(customer = customer.get())
        return walletRepository.save(wallet).toDTO()
    }

    override fun getWalletTransactions(
        walletID: Long,
        from: Long?,
        to: Long?,
        pageable: Pageable
    ): List<TransactionDTO> {
        val wallet = walletRepository.findById(walletID)
        var transactions = listOf<TransactionDTO>()
        if (!wallet.isPresent)
            throw IllegalArgumentException("Wallet does not exist")
        if (from != null && to != null) {
            return transactionRepository.findAllByWalletAndByTimestampBetween(
                wallet.get(),
                Timestamp(from),
                Timestamp(to),
                pageable
            ).map { it -> it.toDTO() }
        }
        if (from != null || to != null)
            throw IllegalArgumentException("Missing from or to for data range")
        return transactionRepository.findAllByWallet(wallet.get(), pageable).map { it -> it.toDTO() }
    }

    override fun performTransaction(transactionDTO: TransactionDTO): TransactionDTO {
        if (transactionDTO.senderID == transactionDTO.receiverID)
            throw IllegalArgumentException("Cannot send money to yourself!")
        val wallets = walletRepository.findAllById(listOf<Long>(transactionDTO.senderID!!, transactionDTO.receiverID!!))
        val senderWallet: Wallet? = wallets.find { it.getId() == transactionDTO.senderID }
        val receiverWallet: Wallet? = wallets.find { it.getId() == transactionDTO.receiverID }
        if (senderWallet == null || receiverWallet == null)
            throw IllegalArgumentException("One of the wallets doesn't exist")
        if (isBalanceInsufficient(senderWallet, transactionDTO.amount!!))
            throw IllegalArgumentException("Sender's balance not enough")
        val transaction = Transaction(
            Timestamp(System.currentTimeMillis()),
            senderWallet,
            receiverWallet,
            transactionDTO.amount
        )
        transferMoney(senderWallet, receiverWallet, transactionDTO.amount)
        return transactionRepository.save(transaction).toDTO()
    }

    override fun getWalletSingleTransaction(walletID: Long, transactionID: Long): TransactionDTO {
        val wallet = walletRepository.findById(walletID)
        if (!wallet.isPresent)
            throw IllegalArgumentException("Wallet does not exist")
        val transaction = transactionRepository.findByWalletAndId(wallet.get(), transactionID)
        if (!transaction.isPresent)
            throw NotFoundException("Transaction does not exist")
        return transaction.get().toDTO()
    }

    fun isBalanceInsufficient(wallet: Wallet, amount: BigDecimal): Boolean {
        return wallet.balance < amount
    }

    fun transferMoney(senderWallet: Wallet, receiverWallet: Wallet, amount: BigDecimal) {
        senderWallet.balance -= amount
        walletRepository.save(senderWallet)
        receiverWallet.balance += amount
        walletRepository.save(receiverWallet)
    }
}
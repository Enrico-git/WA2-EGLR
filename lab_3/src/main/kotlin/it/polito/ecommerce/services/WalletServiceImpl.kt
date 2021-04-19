package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.dto.toDTO
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
class WalletServiceImpl(private val walletRepository: WalletRepository,
private val customerRepository: CustomerRepository,
private val transactionRepository: TransactionRepository): WalletService{

    override fun getWallet(walletID: Long): WalletDTO {
        val walletOpt = walletRepository.findById(walletID)
        if ( ! walletOpt.isPresent)
            throw NotFoundException("Wallet not found")
        val wallet = walletOpt.get()
        return wallet.toDTO()
    }

    override fun addWallet(customerDTO: CustomerDTO): WalletDTO {
        val customerOpt = customerRepository.findById(customerDTO.id!!)
        if (  ! customerOpt.isPresent )
            throw IllegalArgumentException("The customer does not exist")
        val wallet = Wallet(
            customer = customerOpt.get()
        )
        return walletRepository.save(wallet).toDTO()
    }

    override fun performTransaction(transactionDTO: TransactionDTO): TransactionDTO {
        if (transactionDTO.senderID == transactionDTO.receiverID)
            throw IllegalArgumentException("You can't send money to yourself")
        val wallets = walletRepository.findAllById(listOf<Long>(transactionDTO.senderID!!, transactionDTO.receiverID!!))
        val senderWallet: Wallet? = wallets.find{it.getId() == transactionDTO.senderID}
        val receiverWallet: Wallet? = wallets.find{it.getId() == transactionDTO.receiverID}
        if (senderWallet == null || receiverWallet == null)
            throw IllegalArgumentException("One of the wallets doesn't exist")

        if ( isBalanceInsufficient(senderWallet, transactionDTO.amount!!))
            throw IllegalArgumentException("Balance not high enough")

        transferMoney(senderWallet, receiverWallet, transactionDTO.amount)

        val transaction = Transaction(
            timestamp = Timestamp(System.currentTimeMillis()),
            sender = senderWallet,
            receiver = receiverWallet,
            amount = transactionDTO.amount
        )

        return transactionRepository.save(transaction).toDTO()

    }

    override fun getWalletTransactions(walletID: Long, from: Long?, to: Long?, pageable: Pageable): List<TransactionDTO> {

        val walletOpt = walletRepository.findById(walletID)
        if ( ! walletOpt.isPresent)
            throw IllegalArgumentException("Wallet does not exist")

        if ( from != null && to != null) {
            return transactionRepository
                    .findAllByWalletAndByTimestampBetween(walletOpt.get(), Timestamp(from), Timestamp(to), pageable)
                    .map{it.toDTO()}
        }
        if ( from != null || to != null)
            throw IllegalArgumentException("Invalid parameters")

        return transactionRepository.findAllByWallet(walletOpt.get(), pageable).map{it.toDTO()}
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

    override fun getWalletSingleTransaction(walletID: Long, transactionID: Long): TransactionDTO {
        val walletOpt = walletRepository.findById(walletID)
        if ( ! walletOpt.isPresent)
            throw IllegalArgumentException("Wallet does not exist")

        val transaction = transactionRepository.findByWalletAndId(walletOpt.get(), transactionID)
        if ( ! transaction.isPresent )
            throw NotFoundException("The transaction does not exist")
        return transaction.get().toDTO()
    }
}
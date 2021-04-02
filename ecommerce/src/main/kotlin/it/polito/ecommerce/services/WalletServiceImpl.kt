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
import javax.transaction.Transactional

@Service
@Transactional
class WalletServiceImpl( val walletRepository: WalletRepository,
                         val customerRepository: CustomerRepository,
                         val transactionRepository: TransactionRepository

) : WalletService {
    override fun addWallet(customerID: Int): WalletDTO {
        val customer = customerRepository.findById(customerID)
        if(customer.isEmpty)
            throw NotFoundException("Customer not found")

        val wallet = Wallet(null, BigDecimal(0.0), customer.get() )
        return walletRepository.save(wallet).toDTO()
        //TODO check if db is shutted down
    }

    override fun getWallet(walletID: Int): WalletDTO {
        val walletOpt = walletRepository.findById(walletID)
        if(! walletOpt.isPresent)
            throw NotFoundException("Not Found")
        val wallet = walletOpt.get()
        return wallet.toDTO()
    }

    override fun performTransaction(senderID: Int, receiverID: Int, amount: BigDecimal): TransactionDTO {
        if(senderID == receiverID)
            throw IllegalArgumentException("You can't send money to yourself")

        val wallets = walletRepository.findAllById(listOf<Int>(senderID, receiverID))
        val senderWallet = wallets.find { it.id == senderID }
        val receiverWallet = wallets.find { it.id == receiverID }
        if(senderWallet == null || receiverWallet == null){
            throw IllegalArgumentException("Sender or receiver Wallet not Exists")
        }

        if(senderWallet.balance < amount){
            throw IllegalArgumentException("Balance not enough")
        }

        val transaction = Transaction(null,
            Timestamp(System.currentTimeMillis()),
            senderWallet, receiverWallet, amount)

        senderWallet.balance -= amount
        walletRepository.save(senderWallet)
        receiverWallet.balance += amount
        walletRepository.save(receiverWallet)

        return transactionRepository.save(transaction).toDTO()
    }

    override fun getWalletTransactions(walletID: Int, from: Long?, to: Long?): List<TransactionDTO> {
//        val wallet = walletRepository.findById(walletID)
//        if(wallet.isEmpty)
//            throw NotFoundException("Wallet not found")

//        var l1 = wallet.get().transactionsRecv.map { it.toDTO() }
//        var l2 = wallet.get().transactionsSent.map { it.toDTO() }
//
//        if(from!=null && to != null){
//            l1 = l1.filter { it.timestamp <= Timestamp(to)
//                && it.timestamp >= Timestamp(from) }
//            l2 = l2.filter { it.timestamp <= Timestamp(to)
//                && it.timestamp >= Timestamp(from) }
//            return l1 + l2
//        }
//
//        return l1 + l2

        if(from != null && to != null){
            return transactionRepository
                .findAllByWalletAndByTimestampBetween(walletID, Timestamp(from), Timestamp(to))
                .map{it.toDTO()}
        }

        if( (from == null && to != null) || (from != null && to == null)){
            throw IllegalArgumentException("from or to not found")
        }

        return transactionRepository
            .findAllByWallet(walletID)
            .map { it.toDTO() }
    }


}
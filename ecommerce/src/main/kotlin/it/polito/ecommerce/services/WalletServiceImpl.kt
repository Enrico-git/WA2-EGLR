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
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.sql.Timestamp
import javax.transaction.Transactional

@Service
@Transactional
class WalletServiceImpl( val walletRepository: WalletRepository,
                         val customerRepository: CustomerRepository,
                         val transactionRepository: TransactionRepository

) : WalletService {
    override fun addWallet(customerDTO: CustomerDTO): WalletDTO {
        val customerOpt = customerRepository.findById(customerDTO.id!!)
        if(customerOpt.isEmpty)
            throw IllegalArgumentException("The Customer does not exist")

        val wallet = Wallet(
            customer = customerOpt.get()
        )
        return walletRepository.save(wallet).toDTO()
    }

    override fun getWallet(walletID: Long): WalletDTO {
        val walletOpt = walletRepository.findById(walletID)
        if(walletOpt.isEmpty)
            throw NotFoundException("Wallet not found")
        val wallet = walletOpt.get()
        return wallet.toDTO()
    }

    override fun performTransaction(transactionDTO: TransactionDTO): TransactionDTO {
        if(transactionDTO.senderID == transactionDTO.receiverID)
            throw IllegalArgumentException("You can't send money to yourself")

        val wallets = walletRepository.findAllById(listOf<Long>(transactionDTO.senderID!!, transactionDTO.receiverID!!))
        val senderWallet = wallets.find { it.getId() == transactionDTO.senderID }
        val receiverWallet = wallets.find { it.getId() == transactionDTO.receiverID }
        if(senderWallet == null || receiverWallet == null){
            throw IllegalArgumentException("Sender or receiver Wallet not Exists")
        }

        if( isBalanceInsufficient(senderWallet, transactionDTO.amount!!)){
            throw IllegalArgumentException("Balance not enough")
        }

        transferMoney(senderWallet, receiverWallet, transactionDTO.amount)

        val transaction = Transaction(
            timestamp = Timestamp(System.currentTimeMillis()),
            sender = senderWallet,
            receiver = receiverWallet,
            amount = transactionDTO.amount)

        return transactionRepository.save(transaction).toDTO()
    }

    private fun isBalanceInsufficient(wallet: Wallet, amount: BigDecimal): Boolean{
        return wallet.balance < amount
    }

    private fun transferMoney(senderWallet: Wallet, receiverWallet: Wallet, amount: BigDecimal){
        senderWallet.balance -= amount
        walletRepository.save(senderWallet)
        receiverWallet.balance += amount
        walletRepository.save(receiverWallet)
    }


    override fun getWalletTransactions(walletID: Long, from: Long?, to: Long?): Set<TransactionDTO> {
        val walletOpt = walletRepository.findById(walletID)
        if(walletOpt.isEmpty)
            throw IllegalArgumentException("Wallet not found")

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
                .findAllByWalletAndByTimestampBetween(walletOpt.get(), Timestamp(from), Timestamp(to))
                .mapTo(HashSet<TransactionDTO>()){it.toDTO()}
        }

        if( (from == null && to != null) || (from != null && to == null)){
            throw IllegalArgumentException("from or to not found")
        }

        return transactionRepository
            .findAllByWallet(walletOpt.get())
            .mapTo(HashSet<TransactionDTO>()){it.toDTO()}
//        return transactionRepository.findAllBySenderOrReceiver(walletOpt.get(), walletOpt.get())
    }

    override fun getWalletTransaction(walletID: Long, transactionID: Long): TransactionDTO {
        val walletOpt = walletRepository.findById(walletID)
        if (walletOpt.isEmpty)
            throw IllegalArgumentException("Wallet does not exit")

        val transactionOpt = transactionRepository.findByWalletAndId(walletOpt.get(), transactionID)
        if (transactionOpt.isEmpty )
            throw NotFoundException("The transaction not found")

        return transactionOpt.get().toDTO()
    }
}
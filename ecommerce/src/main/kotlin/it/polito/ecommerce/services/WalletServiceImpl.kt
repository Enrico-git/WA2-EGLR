package it.polito.ecommerce.services

import it.polito.ecommerce.domain.Customer
import it.polito.ecommerce.domain.Wallet
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.repositories.CustomerRepository
import it.polito.ecommerce.repositories.TransactionRepository
import it.polito.ecommerce.repositories.WalletRepository
import javassist.NotFoundException
import org.springframework.stereotype.Service
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

        val wallet = Wallet(null, 0.0, customer.get() )
        return walletRepository.save(wallet).toDTO()
    }

    override fun getWallet(walletID: Int): WalletDTO {
        val walletOpt = walletRepository.findById(walletID)
        if(! walletOpt.isPresent)
            throw NotFoundException("Not Found")
        val wallet = walletOpt.get()
        return wallet.toDTO()
    }

    override fun performTransaction(source: Int, dest: Int, amount: Double): Boolean {
        TODO("Not yet implemented")
    }

    override fun getWalletTransactions(walletID: Int, from: Long?, to: Long?): List<TransactionDTO> {
        val wallet = walletRepository.findById(walletID)
        if(wallet.isEmpty)
            throw NotFoundException("Wallet not found")

        var l1 = wallet.get().transactionsRecv.map { it.toDTO() }
        var l2 = wallet.get().transactionsSent.map { it.toDTO() }

        if(from!=null && to != null){
            l1 = l1.filter { it.timestamp <= Timestamp(to)
                && it.timestamp >= Timestamp(from) }
            l2 = l2.filter { it.timestamp <= Timestamp(to)
                && it.timestamp >= Timestamp(from) }
            return l1 + l2
        }

        return l1 + l2
    }


}
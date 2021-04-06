package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.Wallet
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface TransactionRepository : CrudRepository<Transaction, Long> {
    @Query("select t from Transaction t where t.sender = ?1 or t.receiver = ?1")
    fun findAllByWallet(wallet: Wallet): List<Transaction>

//    fun findAllBySenderOrReceiver(sender: Wallet, receiver: Wallet): List<Transaction>

    @Query("select t from Transaction t where (t.sender = ?1 or t.receiver = ?2) and (t.timestamp between ?2 and ?3)")
    fun findAllByWalletAndByTimestampBetween(walletID: Wallet, from: Timestamp, to:Timestamp): List<Transaction>

}
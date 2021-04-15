package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Transaction
import it.polito.ecommerce.domain.Wallet
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.util.*

@Repository
interface TransactionRepository: PagingAndSortingRepository<Transaction, Long> {
    @Query("select t from Transaction t where t.sender = ?1 or t.receiver = ?1")
    fun findAllByWallet(wallet: Wallet, pageable: Pageable): List<Transaction>
    
    @Query("select t from Transaction t where (t.sender = ?1 or t.receiver = ?1) and t.timestamp between ?2 and ?3")
    fun findAllByWalletAndByTimestampBetween(wallet: Wallet, from: Timestamp, to: Timestamp, pageable: Pageable ): List<Transaction>

    @Query("select t from Transaction t where (t.sender = ?1 or t.receiver = ?1) and t.id = ?2")
    fun findByWalletAndId(wallet: Wallet, transactionID: Long): Optional<Transaction>
}
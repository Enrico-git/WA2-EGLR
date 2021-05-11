package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Wallet
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.LockModeType

@Repository
interface WalletRepository : CrudRepository<Wallet, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun findAllById(ids: MutableIterable<Long>): Set<Wallet>

    @Query("select w from User u, Customer c, Wallet w where u.id=c.user and c.id=w.customer and u.username=?1 and w.id=?2")
    fun getWalletByUserAndId(username: String, walletID: Long): Optional<Wallet>
}
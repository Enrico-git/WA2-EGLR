package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Wallet
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.persistence.LockModeType

@Repository
interface WalletRepository: CrudRepository<Wallet, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    override fun findAllById(ids: MutableIterable<Long>): Set<Wallet>
}
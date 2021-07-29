package it.polito.wa2.walletservice.repositories

import it.polito.wa2.walletservice.entities.Wallet
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface WalletRepository : CoroutineCrudRepository <Wallet, ObjectId> {
    suspend fun findByUserID(userID: ObjectId): Wallet?
}

package it.polito.wa2.wallet.repositories

import it.polito.wa2.wallet.entities.Wallet
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface WalletRepository : CoroutineCrudRepository <Wallet, ObjectId> {
}

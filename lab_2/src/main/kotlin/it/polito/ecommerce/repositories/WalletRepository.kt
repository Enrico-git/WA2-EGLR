package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Wallet
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WalletRepository: CrudRepository<Wallet, Int?> {

}
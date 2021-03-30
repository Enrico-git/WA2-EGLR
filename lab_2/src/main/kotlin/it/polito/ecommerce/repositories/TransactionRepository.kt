package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Transaction
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository: CrudRepository<Transaction, Int?> {
}
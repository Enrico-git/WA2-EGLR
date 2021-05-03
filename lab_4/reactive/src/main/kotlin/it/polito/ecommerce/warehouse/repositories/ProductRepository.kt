package it.polito.ecommerce.warehouse.repositories

import it.polito.ecommerce.warehouse.domain.Product
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CoroutineCrudRepository<Product, Long> {
    fun findAllByCategory(category : String): Flow<Product>
}
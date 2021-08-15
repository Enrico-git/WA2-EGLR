package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.domain.Product
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: CoroutineCrudRepository<Product, ObjectId> {

    fun findAllByCategory(category: String, pageable: Pageable): Flow<Product>
    @Query("{ id: { \$exists: true }}")
    fun findAll(pageable: Pageable): Flow<Product>

}
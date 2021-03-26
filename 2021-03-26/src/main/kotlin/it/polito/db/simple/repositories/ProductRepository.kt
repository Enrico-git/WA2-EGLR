package it.polito.db.simple.repositories

import it.polito.db.simple.entities.Product
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: CrudRepository<Product, Long> {
    fun findByPriceBetween(min: Double, max: Double): Iterable<Product>
}
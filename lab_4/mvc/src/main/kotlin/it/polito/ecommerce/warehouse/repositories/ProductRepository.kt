package it.polito.ecommerce.warehouse.repositories

import it.polito.ecommerce.warehouse.domain.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.LockModeType

@Repository
interface ProductRepository : PagingAndSortingRepository<Product, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Product p WHERE p.id=?1")
    fun findByIdWithLock(productID: Long): Optional<Product>
    @Query("SELECT p FROM Product p")
    fun findAllWithPageable(pageable: Pageable): List<Product>
    fun findAllByCategory(category : String, pageable: Pageable): List<Product>
}
package it.polito.ecommerce.warehouse.repositories

import it.polito.ecommerce.warehouse.domain.Product
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ProductRepository: CoroutineCrudRepository<Product,  Long> {

    fun findAllByCategory(category: String): Flow<Product>
    //@Modifying
    //@Query("UPDATE product SET quantity=quantity+:quantity, version=version+1 WHERE id=:productID")
    //fun update(productID: Long, quantity: Long): Product
}

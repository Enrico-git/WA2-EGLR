package it.polito.wa2.orderservice.repositories.orders

import it.polito.wa2.orderservice.domain.Order
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface OrderRepository : CoroutineCrudRepository<Order, ObjectId> {
    fun findAllByBuyer(username: String, pageable: Pageable): Flow<Order>
    fun findByBuyer(username: String): Optional<Order>
}
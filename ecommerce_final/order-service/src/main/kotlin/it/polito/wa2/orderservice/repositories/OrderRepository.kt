package it.polito.wa2.orderservice.repositories

import it.polito.wa2.orderservice.domain.Order
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface OrderRepository : CoroutineCrudRepository<Order, ObjectId> {
    fun findAllByBuyer(userID: ObjectId, pageable: Pageable): Flow<Order>
}
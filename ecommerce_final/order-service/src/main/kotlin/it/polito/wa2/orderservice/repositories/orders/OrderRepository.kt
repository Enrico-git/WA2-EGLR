package it.polito.wa2.orderservice.repositories.orders

import it.polito.wa2.orderservice.domain.Order
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository


@Repository
interface OrderRepository : ReactiveMongoRepository<Order, ObjectId> {
    @Query("{ 'buyer' : ?0 }")
    fun findAllByUsername(username: String, pageable: Pageable): Flow<Order>
}
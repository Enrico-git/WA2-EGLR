package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.domain.Delivery
import it.polito.wa2.warehouseservice.domain.DeliveryDescription
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface DeliveryRepository: CoroutineCrudRepository<Delivery, ObjectId> {
    suspend fun findByOrderID(orderID: ObjectId): Delivery?
    fun findAllByOrderID(orderID: ObjectId): Flow<Delivery>
    suspend fun findByOrderIDAndDescription(orderID: ObjectId, cancellation: DeliveryDescription): Delivery?
}
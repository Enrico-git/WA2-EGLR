package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.domain.Delivery
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface DeliveryRepository: CoroutineCrudRepository<Delivery, ObjectId> {
    fun findAllByOrderID(orderID: ObjectId): Flow<Delivery>
}
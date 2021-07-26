package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.domain.Warehouse
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository: CoroutineCrudRepository<Warehouse, ObjectId> {
}
package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.domain.ProductInfo
import it.polito.wa2.warehouseservice.domain.Warehouse
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository: CoroutineCrudRepository<Warehouse, ObjectId> {
    fun findAllByProducts (productInfo: Set<ProductInfo>): Flow<Warehouse>
}
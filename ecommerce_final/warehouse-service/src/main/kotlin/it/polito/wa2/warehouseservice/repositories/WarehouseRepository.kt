package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.domain.Warehouse
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository: CoroutineCrudRepository<Warehouse, ObjectId> {
    //val template: ReactiveMongoTemplate

    @Query(value = "{'products.productId': ?0}")
    fun findWarehousesByProduct(productID: ObjectId): Flow<Warehouse>?
}
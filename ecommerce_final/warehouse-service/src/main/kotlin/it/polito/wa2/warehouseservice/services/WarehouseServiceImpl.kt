package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WarehouseServiceImpl(
        private val warehouseRepository: WarehouseRepository
): WarehouseService {
    override suspend fun getWarehouses(): Flow<WarehouseDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getWarehouse(warehouseID: ObjectId): WarehouseDTO {
        TODO("Not yet implemented")
    }

    override suspend fun addWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO {
        TODO("Not yet implemented")
    }

    override suspend fun updateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO {
        TODO("Not yet implemented")
    }

    override suspend fun partialUpdateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWarehouses(warehouseID: ObjectId) {
        TODO("Not yet implemented")
    }
}
package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.Warehouse
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.dto.toEntity
import it.polito.wa2.warehouseservice.exceptions.*
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.IllegalArgumentException

@Service
@Transactional
class WarehouseServiceImpl(
        private val warehouseRepository: WarehouseRepository,
        private val mailService: MailService
): WarehouseService {
    override suspend fun getWarehouses(): Flow<WarehouseDTO> {
        return warehouseRepository.findAll().map { it.toDTO() }
    }

    override suspend fun getWarehouse(warehouseID: ObjectId): WarehouseDTO {
        val warehouse = warehouseRepository.findById(warehouseID) ?: throw NotFoundException("Warehouse not found")
        return warehouse.toDTO()
    }

    override suspend fun addWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouse = Warehouse(
                id = null,
                products = emptySet()
        )
        return warehouseRepository.save(warehouse).toDTO()
    }

    override suspend fun updateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouse = warehouseRepository.findById(warehouseID)
        return if(warehouse != null){
            partialUpdateWarehouses(warehouseID, warehouseDTO)
        }else{
            addWarehouse(warehouseDTO)
        }
    }

    override suspend fun partialUpdateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouse = warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
        if(warehouse.products != null) {
            warehouse.products = warehouseDTO.products?.map { it.toEntity() }?.toSet()
            warehouse.products!!.forEach {
                if(it.quantity < it.alarm){
                    //to email admin
                    mailService.notifyAdmin("Order ${it.productId} Notification", it.productId.toString())
                }
            }
        }
        return warehouseRepository.save(warehouse).toDTO()
    }

    override suspend fun deleteWarehouses(warehouseID: ObjectId) {
        warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
        return warehouseRepository.deleteById(warehouseID)
    }
}
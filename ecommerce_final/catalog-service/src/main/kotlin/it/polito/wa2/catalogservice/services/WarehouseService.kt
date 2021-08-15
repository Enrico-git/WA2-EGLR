package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface WarehouseService {

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun getWarehouses(): Flow<WarehouseDTO>
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun getWarehouse(warehouseID: ObjectId): WarehouseDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteWarehouse(warehouseID: ObjectId)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun newWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun patchWarehouse(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateWarehouse(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO
}
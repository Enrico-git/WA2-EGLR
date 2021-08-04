package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize

//TODO remember to insert pageable
interface WarehouseService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getWarehouses(): Flow<WarehouseDTO>
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getWarehouse(warehouseID: String): WarehouseDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteWarehouse(warehouseID: String)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun newWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun patchWarehouse(warehouseID: String, warehouseDTO: WarehouseDTO): WarehouseDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateWarehouse(warehouseID: String, warehouseDTO: WarehouseDTO): WarehouseDTO
}
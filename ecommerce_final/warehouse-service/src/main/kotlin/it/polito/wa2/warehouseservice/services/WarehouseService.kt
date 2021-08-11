package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.ProductInfo
import it.polito.wa2.warehouseservice.dto.ProductInfoDTO
import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface WarehouseService {
//    TODO why customer can see warehouses?
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getWarehouses(): Flow<WarehouseDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getWarehouse(warehouseID: ObjectId): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addWarehouse(warehouseDTO: WarehouseDTO?): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun partialUpdateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteWarehouses(warehouseID: ObjectId)

    suspend fun reserveProductOrAbort(topic: String, productsReservationRequestDTO: ProductsReservationRequestDTO): Boolean? //kafka

    suspend fun reserveProduct(productInfoDTO: ProductInfoDTO): Boolean?
}
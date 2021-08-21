package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.dto.AbortProductReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface WarehouseService {
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun getWarehouses(): Flow<WarehouseDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun getWarehouse(warehouseID: ObjectId): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addWarehouse(warehouseDTO: WarehouseDTO?): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun partialUpdateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteWarehouse(warehouseID: ObjectId)

    suspend fun reserveAllProducts(productsReservationRequestDTO: ProductsReservationRequestDTO): Boolean? //kafka

    suspend fun abortReserveProduct(abortProductReservationRequestDTO: AbortProductReservationRequestDTO): Boolean?
}
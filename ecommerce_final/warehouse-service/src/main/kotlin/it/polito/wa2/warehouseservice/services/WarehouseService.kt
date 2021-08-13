package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.ProductInfo
import it.polito.wa2.warehouseservice.domain.ProductLocation
import it.polito.wa2.warehouseservice.dto.*
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
    suspend fun deleteWarehouses(warehouseID: ObjectId)

    suspend fun reserveProductRequest(topic: String, productsReservationRequestDTO: ProductsReservationRequestDTO): Boolean? //kafka

    suspend fun abortReserveProductRequest(topic: String, abortProductReservationRequestDTO: AbortProductReservationRequestDTO): Boolean? //kafka

    suspend fun reserveProduct(reserveProductDTO: ReserveProductDTO): MutableSet<ProductLocation>?

    suspend fun abortReserveProduct(abortProductReservationRequestDTO: AbortProductReservationRequestDTO): Boolean

    suspend fun mockReserveProductRequest(): String

    suspend fun mockAbortReserveProductRequest(): String
}
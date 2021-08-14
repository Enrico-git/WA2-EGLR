package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.constraintGroups.CreateOrReplaceWarehouse
import it.polito.wa2.warehouseservice.constraintGroups.PartialCreateOrUpdateWarehouse
import it.polito.wa2.warehouseservice.dto.AbortProductReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.services.WarehouseService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/warehouses")
class WarehouseController(
        private val warehouseService: WarehouseService
) {
    /**
     * API endpoint to get all the warehouses
     * @return the flow of the warehouses
     */
    @GetMapping("")
    suspend fun getWarehouses(): Flow<WarehouseDTO> {
        return warehouseService.getWarehouses()
    }

    /**
     * API endpoint to get a warehouse
     * @param warehouseID
     * @return the warehouse object
     */
    @GetMapping("/{warehouseID}")
    suspend fun getWarehouse(@PathVariable warehouseID: ObjectId): WarehouseDTO {
        return warehouseService.getWarehouse(warehouseID)
    }

    /**
     * API endpoint to add a new warehouse
     * @param warehouseDTO
     * @return the warehouse object
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addWarehouse(@RequestBody(required = false) @Validated(PartialCreateOrUpdateWarehouse::class) warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.addWarehouse(warehouseDTO)
    }

    /**
     * API endpoint to update a warehouse or insert it
     * @param warehouseID
     * @param warehouseDTO
     * @return the warehouse object
     */
    @PutMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateWarehouse(@PathVariable warehouseID: ObjectId, @RequestBody @Validated(CreateOrReplaceWarehouse::class) warehouseDTO: WarehouseDTO): WarehouseDTO {
        var counter = 5
        while(counter-- > 0){
            try{
                return warehouseService.updateWarehouses(warehouseID, warehouseDTO)
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Warehouse")
    }

    /**
     * API endpoint to partial update a warehouse
     * @param warehouseDTO
     * @param warehouseDTO
     * @return the warehouse object
     */
    @PatchMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun partialUpdateWarehouse(@PathVariable warehouseID: ObjectId, @RequestBody @Validated(PartialCreateOrUpdateWarehouse::class) warehouseDTO: WarehouseDTO): WarehouseDTO {
        var counter = 5
        while(counter-- > 0){
            try{
                return warehouseService.partialUpdateWarehouses(warehouseID, warehouseDTO)
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Warehouse")
    }

    /**
     * API endpoint to remove a warehouse
     * @param warehouseID
     * @return nothing
     */
    @DeleteMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteWarehouse(@PathVariable warehouseID: ObjectId) {
        var counter = 5
        while(counter-- > 0){
            try{
                return warehouseService.deleteWarehouses(warehouseID)
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Warehouse")
    }

    @PostMapping("/reserveproducts")
    suspend fun mockReserveProductRequest(@RequestBody productsReservationRequestDTO: ProductsReservationRequestDTO): String?{
        val result = warehouseService.mockReserveProductRequest()
        println(result)
        return result
    }

    @PostMapping("/abortreserveproducts")
    suspend fun mockAbortReserveProductRequest(@RequestBody abortProductReservationRequestDTO: AbortProductReservationRequestDTO): String? {
        val result = warehouseService.mockAbortReserveProductRequest()
        println(result)
        return result
    }

}
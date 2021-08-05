package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.services.WarehouseService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/warehouses")
class WarehouseController(
        private val warehouseService: WarehouseService
) {
    /**
     * API endpoint to get all the warehouses
     * @param nothing
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
    suspend fun getWarehouse(@PathVariable warehouseID: String): WarehouseDTO {
        return warehouseService.getWarehouse(ObjectId(warehouseID))
    }

    /**
     * API endpoint to add a new warehouse
     * @param WarehouseDTO
     * @return the warehouse object
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addWarehouse(@RequestBody warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.addWarehouse(warehouseDTO)
    }

    /**
     * API endpoint to update a warehouse or insert it
     * @param WarehouseId, @param WarehouseDTO
     * @return the warehouse object
     */
    @PutMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateWarehouse(@PathVariable warehouseID: String, @RequestBody warehouseDTO: WarehouseDTO): WarehouseDTO {
        var counter = 5
        while(counter-- > 0){
            try{
                return warehouseService.updateWarehouses(ObjectId(warehouseID), warehouseDTO)
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Warehouse")
    }

    /**
     * API endpoint to partial update a warehouse
     * @param WarehouseId, @param WarehouseDTO
     * @return the warehouse object
     */
    @PatchMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun partialUpdateWarehouse(@PathVariable warehouseID: String, @RequestBody warehouseDTO: WarehouseDTO): WarehouseDTO {
        var counter = 5
        while(counter-- > 0){
            try{
                return warehouseService.partialUpdateWarehouses(ObjectId(warehouseID), warehouseDTO)
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Warehouse")
    }

    /**
     * API endpoint to remove a warehouse
     * @param WarehouseId
     * @return nothing
     */
    @DeleteMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteWarehouse(@PathVariable warehouseID: String) {
        var counter = 5
        while(counter-- > 0){
            try{
                return warehouseService.deleteWarehouses(ObjectId(warehouseID))
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Warehouse")
    }

    @PostMapping("/andonio")
    suspend fun andonio(@RequestBody productsReservationRequestDTO: ProductsReservationRequestDTO): Boolean?{
        return warehouseService.reserveProductOrAbort("reserve_products", productsReservationRequestDTO)
    }

}
package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.constraintGroups.CreateOrReplaceWarehouse
import it.polito.wa2.catalogservice.constraintGroups.PartialCreateOrUpdateWarehouse
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import it.polito.wa2.catalogservice.services.WarehouseService
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
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
    @ResponseStatus(HttpStatus.OK)
    suspend fun getWarehouses(): Flow<WarehouseDTO> {
        return warehouseService.getWarehouses()
    }

    /**
     * API endpoint to get a warehouse
     * @param warehouseID
     * @return the warehouse object
     */
    @GetMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getWarehouse(@PathVariable warehouseID: ObjectId): WarehouseDTO {
        return warehouseService.getWarehouse(warehouseID)
    }

    /**
     * API endpoint to remove a warehouse
     * @param warehouseID
     * @return nothing
     */
    @DeleteMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteWarehouse(@PathVariable warehouseID: ObjectId) {
        return warehouseService.deleteWarehouse(warehouseID)
    }

    /**
     * API endpoint to add a new warehouse
     * @param warehouseDTO
     * @return the warehouse object
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newWarehouse(@RequestBody(required = false) @Validated(PartialCreateOrUpdateWarehouse::class) warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.newWarehouse(warehouseDTO)
    }

    /**
     * API endpoint to partial update a warehouse
     * @param warehouseDTO
     * @param warehouseDTO
     * @return the warehouse object
     */
    @PatchMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun patchWarehouse(@PathVariable warehouseID: ObjectId,
                               @RequestBody @Validated(PartialCreateOrUpdateWarehouse::class) warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.patchWarehouse(warehouseID,warehouseDTO)
    }

    /**
     * API endpoint to update a warehouse or insert it
     * @param warehouseID
     * @param warehouseDTO
     * @return the warehouse object
     */
    @PutMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateWarehouse(@PathVariable warehouseID: ObjectId,
                                @RequestBody @Validated(CreateOrReplaceWarehouse::class) warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.updateWarehouse(warehouseID,warehouseDTO)
    }
}
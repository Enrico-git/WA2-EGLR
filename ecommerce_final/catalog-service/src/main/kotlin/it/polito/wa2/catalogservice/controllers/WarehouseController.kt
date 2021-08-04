package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.WarehouseDTO
import it.polito.wa2.catalogservice.services.WarehouseService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/warehouses")
class WarehouseController(
    private val warehouseService: WarehouseService
) {
    //RETRIEVE THE LIST OF WAREHOUSES
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getWarehouses(): Flow<WarehouseDTO> {
        return warehouseService.getWarehouses()
    }

    //RETRIEVE INFO ABOUT A WAREHOUSE GIVEN ITS ID
    @GetMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getWarehouse(@PathVariable warehouseID: String): WarehouseDTO {
        return warehouseService.getWarehouse(warehouseID)
    }

    //DELETE A WAREHOUSE GIVEN ITS ID, IF POSSIBLE
    @DeleteMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteWarehouse(@PathVariable warehouseID: String) {
        return warehouseService.deleteWarehouse(warehouseID)
    }

    //CREATE A NEW WAREHOUSE WITH A LIST OF PRODUCTS
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newWarehouse(@RequestBody warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.newWarehouse(warehouseDTO)
    }

    //PARTIALLY UPDATE A WAREHOUSE GIVEN ITS ID
    @PatchMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun patchWarehouse(@PathVariable warehouseID: String,
                               @RequestBody warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.patchWarehouse(warehouseID,warehouseDTO)
    }

    //UPDATE A WAREHOUSE GIVEN ITS ID, OR ADD A NEW ONE IF THE ID DOES NOT EXIST
    @PutMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateWarehouse(@PathVariable warehouseID: String,
                                @RequestBody warehouseDTO: WarehouseDTO): WarehouseDTO {
        return warehouseService.updateWarehouse(warehouseID,warehouseDTO)
    }
}
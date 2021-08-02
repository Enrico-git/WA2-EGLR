package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.ProductInfo
import it.polito.wa2.warehouseservice.domain.Warehouse
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.ProductInfoDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.dto.toEntity
import it.polito.wa2.warehouseservice.exceptions.*
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
                products = mutableSetOf()
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
        val products: MutableSet<ProductInfoDTO>? = warehouse.products?.map{it.toDTO()}?.toMutableSet()
        val oldProductIds = products?.map{it.id} //oldProductIds contains the ids already present in the warehouse
        val warehouseDTOIds = warehouseDTO.products?.map{it.id} // warehouseDTOIds contains the ids in the warehouseDTO (used to modify the warehouse)
        val warehouseIds = oldProductIds?.intersect(warehouseDTOIds!!.toSet()) //warehouseIds contains the ids of the product already present in the warehouse which are present also in warehouseDTO
        val oldProductInfos = products?.filter { warehouseIds!!.contains(it.id) } //oldProductInfos contains the ProductInfos whose ids are in the warehouse (already present to delete)
        val updatedOldProductInfos = warehouseDTO.products?.filter { warehouseIds!!.contains(it.id) } //updatedOldProductInfos contains the ProductInfos whose ids are in the warehouseDTO (they are used to update the already present DTO)
        if(oldProductInfos!!.isNotEmpty()) { //modify the already present ProductInfo
            oldProductInfos.forEach { products!!.remove(it) } //remove the ProductInfo to update
            updatedOldProductInfos!!.forEach{
                val productInfo = ProductInfo(  //create a new ProductInfo to update the already present in the warehouse
                        productId = ObjectId(it.id),
                        alarm = it.alarm,
                        quantity = it.quantity
                )
                products!!.add(productInfo.toDTO())
            }
        }
        val newProductIds = warehouseDTOIds?.subtract(oldProductIds!!.toSet()) //newProductIds are the ids of the new product to add (not already present in the warehouse)
        val newProductInfos = warehouseDTO.products?.filter { newProductIds!!.contains(it.id) } //newProductInfos are the products whose ids are newProductIds
        if(newProductInfos!!.isNotEmpty()) {
            newProductInfos.forEach{
                products!!.add(it)
                println(products)
            }
        }
        products!!.forEach {
            if(it.quantity <= it.alarm)
                mailService.notifyAdmin("Warehouse $warehouseID Notification", it.id)
        }
        warehouse.products = products.map { it.toEntity() }.toMutableSet()
        return warehouseRepository.save(warehouse).toDTO()
    }

    override suspend fun deleteWarehouses(warehouseID: ObjectId) {
        warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
        return warehouseRepository.deleteById(warehouseID)
    }
}
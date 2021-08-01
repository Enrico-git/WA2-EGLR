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
                products = emptySet()
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
//        val warehouse = warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
//        if(warehouse.products != null) {
//            val productInfoIds = warehouseDTO.products!!.map{it.id}
//            val oldProductInfo = warehouse.products?.filter{
//                productInfoIds.contains(it.productId.toString())
//            }
//            if(oldProductInfo!!.isEmpty()){
//                warehouse.products = warehouseDTO.products.map { it.toEntity() }?.toSet()
//                warehouse.products!!.forEach {
//                    if(it.quantity < it.alarm){
//                        //to email admin
//                        mailService.notifyAdmin("Order ${it.productId} Notification", it.productId.toString())
//                    }
//                }
//            }else{
//                warehouse.products!!.minus(oldProductInfo)
//                val newProductInfo = warehouseDTO.products.filter {
//                    it.id == oldProductInfo.first().productId.toString()
//                }
//                oldProductInfo.first().alarm = newProductInfo.first().alarm ?: oldProductInfo.first().alarm
//                oldProductInfo.first().quantity = newProductInfo.first().quantity
//            }
//            warehouse.products = warehouseDTO.products?.map { it.toEntity() }?.toSet()
//            warehouse.products!!.forEach {
//                if(it.quantity < it.alarm){
//                    //to email admin
//                    mailService.notifyAdmin("Order ${it.productId} Notification", it.productId.toString())
//                }
//            }
//        }
        val warehouse = warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
        var products = warehouse.products?.map{it.toDTO()}
        val oldProductInfos = (products?.intersect(warehouseDTO.products!!.toSet()))
        if(oldProductInfos!!.isNotEmpty()) { //modify the already present ProductInfo
            println(oldProductInfos.forEach{it.id})
            val newOldProductInfos = setOf<ProductInfo>()
            oldProductInfos.forEach {
                newOldProductInfos.plusElement(ProductInfo(
                        productId = ObjectId(it.id),
                        alarm = it.alarm,
                        quantity = it.quantity
                ))
                products!!.minusElement(it.toEntity())
            }
            newOldProductInfos.forEach{
                products!!.plusElement(it)
                println(products)
            }
        }
        val newProductInfos = warehouseDTO.products?.subtract(oldProductInfos)
        if(newProductInfos!!.isNotEmpty()) {
            newProductInfos.forEach{
                products!!.plusElement(it.toEntity())
                println(products)
            }

//            newProductInfos.map {
//                warehouse.products!!.plus(it.toEntity())
//            }
        }
        products!!.forEach {
            if(it.quantity <= it.alarm)
                mailService.notifyAdmin("Order ${it.id} Notification", it.id)
        }
        warehouse.products = products.map { it.toEntity() }.toSet()
        return warehouseRepository.save(warehouse).toDTO()
    }

    override suspend fun deleteWarehouses(warehouseID: ObjectId) {
        warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
        return warehouseRepository.deleteById(warehouseID)
    }
}
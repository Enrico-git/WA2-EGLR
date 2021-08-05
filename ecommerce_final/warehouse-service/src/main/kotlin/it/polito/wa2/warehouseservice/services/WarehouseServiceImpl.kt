package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.ProductInfo
import it.polito.wa2.warehouseservice.domain.Warehouse
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.ProductInfoDTO
import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.dto.toEntity
import it.polito.wa2.warehouseservice.exceptions.*
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import kotlinx.coroutines.flow.*
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
@Transactional
class WarehouseServiceImpl(
        private val warehouseRepository: WarehouseRepository,
        private val mailService: MailService
): WarehouseService {

    @Value("\${spring.kafka.retryDelay}")
    private val retryDelay: Long = 0

    class WarehouseComparator(val productId: ObjectId): Comparator<Warehouse>{
        override fun compare(p0: Warehouse, p1: Warehouse): Int {
            val product1 = p0.products!!.find { it.productId == productId }
            val product2 = p1.products!!.find { it.productId == productId }

            return if(product1!!.quantity!! >= product2!!.quantity!!)
                1
            else
                -1

        }

    }

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
                products.add(productInfo.toDTO())
            }
        }
        val newProductIds = warehouseDTOIds?.subtract(oldProductIds!!.toSet()) //newProductIds are the ids of the new product to add (not already present in the warehouse)
        val newProductInfos = warehouseDTO.products?.filter { newProductIds!!.contains(it.id) } //newProductInfos are the products whose ids are newProductIds
        if(newProductInfos!!.isNotEmpty()) {
            newProductInfos.forEach{
                products.add(it)
                println(products)
            }
        }
        products.forEach {
            if(it.quantity <= it.alarm!!)
                mailService.notifyAdmin("Warehouse $warehouseID Notification", it.id)
        }
        warehouse.products = products.map { it.toEntity() }.toMutableSet()
        return warehouseRepository.save(warehouse).toDTO()
    }

    override suspend fun deleteWarehouses(warehouseID: ObjectId) {
        warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
        return warehouseRepository.deleteById(warehouseID)
    }

    override suspend fun reserveProduct(productInfoDTO: ProductInfoDTO): Boolean? {
        println("reserveProduct")
        var warehouse = warehouseRepository.findWarehousesByProduct(ObjectId(productInfoDTO.id))?.toList()
        warehouse?.forEach{ println(it.products)}
        warehouse = warehouse?.sortedWith(WarehouseComparator(ObjectId(productInfoDTO.id)))
        warehouse?.forEach{ println(it.products)}

        return false
//        val whs = warehouse?.map{ wh->
//            wh.products?.filter { it ->
//                it.productId == ObjectId(productInfoDTO.id)
//            }
//            wh.products?.first()
//        }
//        whs?.toSet()?.sortedBy { it?.quantity }


//        first() ?: return false
//        val warehouseProductInfo = warehouse.products?.find { it.productId == ObjectId(productInfoDTO.id) } ?: return false
//        if((warehouseProductInfo.quantity!! < productInfoDTO.quantity)) return false
//        warehouse.products!!.find { it.productId == ObjectId(productInfoDTO.id) }!!.quantity = warehouse.products!!.find { it.productId == ObjectId(productInfoDTO.id) }!!.quantity?.minus(productInfoDTO.quantity) ?: return false
//        warehouseRepository.save(warehouse)
//        return true
    }

    /**
     * wh = {id:1,  products = [{productId: 2, alarm = 3, qty: 10}, {productId:3, alarm:5, qty = 8}]}
     * productInfoDTO = {productId: 2, alarm = 3, qty: 10}
     */
    override suspend fun reserveProductOrAbort(topic: String, productsReservationRequestDTO: ProductsReservationRequestDTO): Boolean? {
        if(Timestamp(productsReservationRequestDTO.timestamp.time + retryDelay) > Timestamp(System.currentTimeMillis()) )
            return null

        println("ReserveProductOrAbort")
        when(topic){
            "reserve_products" -> {

                productsReservationRequestDTO.products.forEach{
                    println(it.id)
                    val productInfoDTO = ProductInfoDTO(
                          id = it.id,
                          alarm = null,
                          quantity = it.amount
                    )
                    return reserveProduct(productInfoDTO)
                }
            }
            "abort_products_reservation" -> {

            }
         }
        TODO()
    }
}
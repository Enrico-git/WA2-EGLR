package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.Delivery
import it.polito.wa2.warehouseservice.domain.ProductLocation
import it.polito.wa2.warehouseservice.domain.Warehouse
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.*
import it.polito.wa2.warehouseservice.exceptions.*
import it.polito.wa2.warehouseservice.repositories.DeliveryRepository
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import kotlinx.coroutines.flow.*
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import kotlin.math.absoluteValue

@Service
@Transactional
class WarehouseServiceImpl(
        private val warehouseRepository: WarehouseRepository,
        private val productRepository: ProductRepository,
        private val mailService: MailService,
        private val deliveryRepository: DeliveryRepository
): WarehouseService {

    @Value("\${spring.kafka.retryDelay}")
    private val retryDelay: Long = 0

    class WarehouseComparator(val productId: ObjectId): Comparator<Warehouse>{
        override fun compare(p0: Warehouse, p1: Warehouse): Int {
            val product1 = p0.products.find { it.productId == productId }
            val product2 = p1.products.find { it.productId == productId }

            return if(product1!!.quantity >= product2!!.quantity)
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

    override suspend fun addWarehouse(warehouseDTO: WarehouseDTO?): WarehouseDTO {
        if (warehouseDTO != null && warehouseDTO.products!!.isNotEmpty() &&
            productRepository.findAllById(warehouseDTO.products.map { ObjectId(it.id) }).toSet().size != warehouseDTO.products.size )
                throw IllegalArgumentException("Product list contains non existent products")
        val warehouse = Warehouse(
                id = if (warehouseDTO?.id != null) ObjectId(warehouseDTO.id) else null,
                products = warehouseDTO?.products?.map { it.toEntity() }?.toMutableSet() ?: mutableSetOf()
        )
        return warehouseRepository.save(warehouse).toDTO()
    }

    override suspend fun updateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouse = warehouseRepository.findById(warehouseID)
        return if(warehouse != null){
            partialUpdateWarehouses(warehouseID, warehouseDTO)
        } else
            addWarehouse(warehouseDTO)
    }

    override suspend fun partialUpdateWarehouses(warehouseID: ObjectId, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val warehouse = warehouseRepository.findById(warehouseID) ?: throw IllegalArgumentException("Warehouse not found")
        val products = warehouseDTO.products!!.map{it.toEntity()}.toMutableSet()
        if (warehouseDTO.products.isNotEmpty() && productRepository.findAllById(warehouseDTO.products.map { ObjectId(it.id) }).toSet().size != warehouseDTO.products.size )
            throw IllegalArgumentException("Product list contains non existent products")

        warehouse.products.removeAll { existingProd -> products.any { it.productId == existingProd.productId } }
        warehouse.products.addAll(products)
        warehouseRepository.save(warehouse)
        warehouse.products.forEach {
            if(it.quantity <= it.alarm)
                mailService.notifyAdmin("Warehouse $warehouseID Notification", it.productId.toHexString())
        }
        return warehouse.toDTO()


//        val products: MutableSet<ProductInfoDTO>? = warehouse.products?.map{it.toDTO()}?.toMutableSet()
//        val oldProductIds = products?.map{it.id} //oldProductIds contains the ids already present in the warehouse
//        val warehouseDTOIds = warehouseDTO.products?.map{it.id} // warehouseDTOIds contains the ids in the warehouseDTO (used to modify the warehouse)
//        val warehouseIds = oldProductIds?.intersect(warehouseDTOIds!!.toSet()) //warehouseIds contains the ids of the product already present in the warehouse which are present also in warehouseDTO
//        val oldProductInfos = products?.filter { warehouseIds!!.contains(it.id) } //oldProductInfos contains the ProductInfos whose ids are in the warehouse (already present to delete)
//        val updatedOldProductInfos = warehouseDTO.products?.filter { warehouseIds!!.contains(it.id) } //updatedOldProductInfos contains the ProductInfos whose ids are in the warehouseDTO (they are used to update the already present DTO)
//        if(oldProductInfos!!.isNotEmpty()) { //modify the already present ProductInfo
//            oldProductInfos.forEach { products!!.remove(it) } //remove the ProductInfo to update
//            updatedOldProductInfos!!.forEach{
//                val productInfo = ProductInfo(  //create a new ProductInfo to update the already present in the warehouse
//                        productId = ObjectId(it.id),
//                        alarm = it.alarm,
//                        quantity = it.quantity
//                )
//                products.add(productInfo.toDTO())
//            }
//        }
//        val newProductIds = warehouseDTOIds?.subtract(oldProductIds!!.toSet()) //newProductIds are the ids of the new product to add (not already present in the warehouse)
//        val newProductInfos = warehouseDTO.products?.filter { newProductIds!!.contains(it.id) } //newProductInfos are the products whose ids are newProductIds
//        if(newProductInfos!!.isNotEmpty()) {
//            newProductInfos.forEach{
//                products.add(it)
//                println(products)
//            }
//        }
//        products.forEach {
//            if(it.quantity <= it.alarm!!)
//                mailService.notifyAdmin("Warehouse $warehouseID Notification", it.id)
//        }
//        warehouse.products = products.map { it.toEntity() }.toMutableSet()
//        return warehouseRepository.save(warehouse).toDTO()
    }

    override suspend fun deleteWarehouses(warehouseID: ObjectId) {
        warehouseRepository.findById(warehouseID) ?: throw NotFoundException("Warehouse not found")
        return warehouseRepository.deleteById(warehouseID)
    }

    override suspend fun reserveProduct(reserveProductDTO: ReserveProductDTO): ProductLocation? {
        val warehouses = warehouseRepository.findWarehousesByProduct(ObjectId(reserveProductDTO.id)).toList().sortedWith(WarehouseComparator(ObjectId(reserveProductDTO.id)))
//        warehouse.forEach{ println(it.products)}
//        warehouse = warehouse.sortedWith(WarehouseComparator(ObjectId(productInfoDTO.id)))
//        warehouse.forEach{ println(it.products)}
        var qty = reserveProductDTO.amount
        if (warehouses.fold(0) { acc, warehouse ->  acc + warehouse.products.find { it.productId == ObjectId(reserveProductDTO.id) }!!.quantity} < reserveProductDTO.amount)
            return null
        val productLocation = ProductLocation(
                productID = reserveProductDTO.id,
                amount = reserveProductDTO.amount,
                warehouseID = mutableSetOf()
        )
        warehouses.map { wh ->
            if(qty > 0){
                val warehouseProductInfo: ProductInfoDTO = wh.products.find { it.productId == ObjectId(reserveProductDTO.id) }!!.toDTO()
                warehouseProductInfo.quantity -= qty
                productLocation.warehouseID.plus(wh.id.toString())
                //delivery.products.plus(ProductLocation(reserveProductDTO.id, wh.id.toString(), warehouseProductInfo.quantity))
                if(warehouseProductInfo.quantity < 0){
                    qty -=  warehouseProductInfo.quantity.absoluteValue
                    warehouseProductInfo.quantity = 0
                }
                wh.products.find { it.productId == ObjectId(reserveProductDTO.id) }!!.quantity = warehouseProductInfo.quantity
            }
        }
        warehouseRepository.saveAll(warehouses)
        return productLocation

//            wh.products.find { it.productId == ObjectId(reserveProductDTO.id) }!!.quantity -= qty
//            if(wh.products.find { it. })
//        }
//        while(qty > 0){
//            val warehouseProductInfo = warehouses.first().products.find{ it.productId == ObjectId(reserveProductDTO.id)}
//            warehouseProductInfo!!.quantity -= qty
//            if(warehouseProductInfo!!.quantity < 0){
//                qty -= warehouseProductInfo.quantity.absoluteValue
//                warehouseProductInfo.quantity = 0
//                delivery.products.plus(ProductLocation(reserveProductDTO.id, warehouses.first().id.toString(), reserveProductDTO.amount))
//            }else
//                break
//        }

//            
//
//        }
//        warehouses.forEach{ it ->
//            it.products.find{it.productId == ObjectId(reserveProductDTO.id)}!!.quantity
//        }
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


    override suspend fun abortReserveProduct(reserveProductDTO: ReserveProductDTO, orderId: String): Boolean {
        val delivery = deliveryRepository.findByOrderId(ObjectId(orderId)) ?: return false
        delivery.products.forEach{ ot ->
            ot.warehouseID.forEach{
                val wh = warehouseRepository.findById(ObjectId(it)) ?: return false
                wh.products.find{ iit ->
                    iit.productId == ObjectId(reserveProductDTO.id)
                }!!.quantity += ot.amount
                warehouseRepository.save(wh)
            }
        }
        return true
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
                val delivery = DeliveryDTO(
                        id = null,
                        orderId = productsReservationRequestDTO.orderID,
                        timestamp = productsReservationRequestDTO.timestamp,
                        products = mutableSetOf()
                )
                productsReservationRequestDTO.products.forEach {
                    val productLocation = reserveProduct(it)
                    if (productLocation != null)
                        delivery.products.plus(productLocation)
                    else
                        return false
                }
                return true
//                productsReservationRequestDTO.products.forEach{
//                    println(it.id)
//                    val productInfoDTO = ProductInfoDTO(
//                          id = it.id,
//                          alarm = null,
//                          quantity = it.amount
//                    )
//                    return reserveProduct(productInfoDTO)
//                }
            }
            "abort_products_reservation" -> {
                productsReservationRequestDTO.products.forEach {
                    val result = abortReserveProduct(it, productsReservationRequestDTO.orderID)
                    if(!result)
                        return false
                }
                return true
            }
            else ->
                return false
         }

    }
}
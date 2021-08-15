package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.comparators.WarehouseComparator
import it.polito.wa2.warehouseservice.domain.*
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
    }

    override suspend fun deleteWarehouse(warehouseID: ObjectId) {
        warehouseRepository.findById(warehouseID) ?: throw NotFoundException("Warehouse not found")
        return warehouseRepository.deleteById(warehouseID)
    }


    override suspend fun reserveAllProducts(productsReservationRequestDTO: ProductsReservationRequestDTO): Boolean? {
        if(Timestamp(productsReservationRequestDTO.timestamp.time + retryDelay) < Timestamp(System.currentTimeMillis()) )
            return null

        val deliveries = deliveryRepository.findAllByOrderID(productsReservationRequestDTO.orderID).toSet()
        if (deliveries.isNotEmpty())
            return null
        val productIDS = productsReservationRequestDTO.products.map { ObjectId(it.id) }.asFlow()
        val warehouses = warehouseRepository.findWarehousesByProductsIDIn(productIDS).toSet()

        if (warehouses.isEmpty())
            return false

        val productLocations = mutableSetOf<ProductLocation>()

        productsReservationRequestDTO.products.forEach { product ->
            warehouses.sortedWith(WarehouseComparator(ObjectId(product.id)))
            var amount = product.amount
            warehouses.forEach InnerLoop@{
                if (amount > 0) {
                    val whProd = it.products.find { whProd -> whProd.productId == ObjectId(product.id) } ?: return false
                    val removed: Int
                    if (whProd.quantity > amount) {
                        whProd.quantity -= amount
                        removed = amount
                        amount = 0
                    } else {
                        removed = whProd.quantity
                        whProd.quantity = 0
                        amount -= removed
                    }
                    productLocations.add(
                        ProductLocation(
                            productID = product.id,
                            warehouseID = it.id!!.toHexString(),
                            amount = removed
                        )
                    )
                }
                else
                    return@InnerLoop
            }
            if (amount > 0)
                return false
        }
        warehouseRepository.saveAll(warehouses).collect()
        deliveryRepository.save(Delivery(
            id = null,
            orderID = productsReservationRequestDTO.orderID,
            products = productLocations,
            description = DeliveryDescription.RESERVATION,
            timestamp = Timestamp(System.currentTimeMillis())
        ))

        warehouses.forEach { wh ->
            wh.products.filter { productIDS.toSet().contains(it.productId) }.forEach {
                if(it.quantity < it.alarm)
                    mailService.notifyAdmin("Warehouse ${wh.id} Notification", it.productId.toHexString())
            }
        }
        return true
    }

    override suspend fun abortReserveProduct(abortProductReservationRequestDTO: AbortProductReservationRequestDTO): Boolean? {
        if(Timestamp(abortProductReservationRequestDTO.timestamp.time + retryDelay) < Timestamp(System.currentTimeMillis()) )
            return null

        val deliveries = deliveryRepository.findAllByOrderID(abortProductReservationRequestDTO.orderID).toSet()
        if (deliveries.any{ it.description == DeliveryDescription.CANCELLATION })
            return null

        val delivery = deliveries.find{it.description == DeliveryDescription.RESERVATION} ?: return null
        val warehousesIDS = delivery.products.map { ObjectId(it.warehouseID) }.asFlow()
        val warehouses: Set<Warehouse>
        try {
            warehouses = warehouseRepository.findAllById(warehousesIDS).toSet()
        } catch (e: IllegalArgumentException){
            return false
        }
        delivery.products.forEach { product ->
            warehouses
                .find { it.id == ObjectId(product.warehouseID) }!!
                .products.find { it.productId == ObjectId(product.productID) }!!
                .quantity += product.amount
        }
        warehouseRepository.saveAll(warehouses.asFlow()).collect()
        deliveryRepository.save(Delivery(
            id = null,
            orderID = delivery.orderID,
            timestamp = Timestamp(System.currentTimeMillis()),
            products = delivery.products,
            description = DeliveryDescription.CANCELLATION
        ))
        return true
    }

}
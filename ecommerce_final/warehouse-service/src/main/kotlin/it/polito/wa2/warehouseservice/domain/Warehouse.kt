package it.polito.wa2.warehouseservice.domain

import it.polito.wa2.warehouseservice.dto.ProductInfoDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "warehouses")
data class Warehouse (
        @Id
        val id: ObjectId?,
        var products: MutableSet<ProductInfo>?,
        @Version
        val version: Long? = null
)

data class ProductInfo(
        val productId: ObjectId, //ProductId
        var alarm: Int?,
        var quantity: Int?
)

fun Warehouse.toDTO() = WarehouseDTO(
        id = id.toString(),
        products = products?.map{ ProductInfoDTO(it.productId.toString(), it.alarm!!, it.quantity!!) }!!.toMutableSet()
)

fun ProductInfo.toDTO() = ProductInfoDTO(
        id = productId.toString(),
        alarm = alarm!!,
        quantity = quantity!!
)
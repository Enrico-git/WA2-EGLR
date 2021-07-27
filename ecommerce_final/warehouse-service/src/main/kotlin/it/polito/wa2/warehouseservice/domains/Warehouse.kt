package it.polito.wa2.warehouseservice.domains

import it.polito.wa2.warehouseservice.dto.ProductInfoDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "warehouses")
class Warehouse (
        @Id
        val id: ObjectId?,
        val products: List<ProductInfo>?,
        @Version
        val version: Long = Long.MIN_VALUE
)

data class ProductInfo(
        val productId: ObjectId, //ProductId
        val alarm: Int,
        val quantity: Int
)

fun Warehouse.toDTO() = WarehouseDTO(
        id = id.toString(),
        products = products?.map{ ProductInfoDTO(it.productId.toString(), it.alarm, it.quantity) }
)
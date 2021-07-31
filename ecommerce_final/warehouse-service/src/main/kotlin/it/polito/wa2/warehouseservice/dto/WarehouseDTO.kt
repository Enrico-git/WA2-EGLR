package it.polito.wa2.warehouseservice.dto

import it.polito.wa2.warehouseservice.domain.Warehouse
import org.bson.types.ObjectId

data class WarehouseDTO (
        val id: String?,
        val products: Set<ProductInfoDTO>?
)

fun WarehouseDTO.toEntity() = Warehouse(
        id = ObjectId(id),
        products = products?.map{it.toEntity()}?.toSet()
)
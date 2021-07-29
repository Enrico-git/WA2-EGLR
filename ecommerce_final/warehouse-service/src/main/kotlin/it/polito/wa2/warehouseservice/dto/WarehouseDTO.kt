package it.polito.wa2.warehouseservice.dto

data class WarehouseDTO (
        val id: String?,
        val products: Set<ProductInfoDTO>?
)
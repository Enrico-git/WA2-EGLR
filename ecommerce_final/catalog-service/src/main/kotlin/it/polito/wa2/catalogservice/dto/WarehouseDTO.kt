package it.polito.wa2.catalogservice.dto

data class WarehouseDTO (
    val id: String?,
    val products: Set<ProductInfoDTO>?
)
package it.polito.wa2.warehouseservice.domain

data class ProductLocation (
        val productID: String,
        val warehouseID: MutableSet<String>,
        val amount: Int
)
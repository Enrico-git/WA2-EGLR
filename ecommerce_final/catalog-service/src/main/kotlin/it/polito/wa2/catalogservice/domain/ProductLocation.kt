package it.polito.wa2.catalogservice.domain

//[{banane, wh1, 2}, {arance, wh2, 2}, {banane, wh3, 3}]
data class ProductLocation(
    val productID: String,
    val warehouseID: String,
    val amount: Long
)
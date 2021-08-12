package it.polito.wa2.orderservice.domain

//{banane, wh1, 2}
data class ProductLocation(
    val productID: String,
    val warehouseID: String,
    val amount: Long
)

package it.polito.wa2.orderservice.domain

/**
 * Order
 *       id
 *       buyer
 *       status
 *       products: [{banane, 5, 2.00$}, {arance, 2, 4$}]
 *       delivery: { address: "via di qua", productsLocation: [{banane, wh1, 2}, {arance, wh2, 2}, {banane, wh3, 3}]
 */
data class Delivery(
        val shippingAddress: String,
        val productWarehouseLocation: Set<ProductLocation>?
)
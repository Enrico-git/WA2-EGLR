package it.polito.wa2.warehouseservice.dto

import it.polito.wa2.warehouseservice.domain.ProductInfo
import org.bson.types.ObjectId


data class ProductInfoDTO(
        val id: String,
        val alarm: Int,
        val quantity: Int
)

fun ProductInfoDTO.toEntity() = ProductInfo(
        productId = ObjectId(id),
        alarm = alarm,
        quantity = quantity
)

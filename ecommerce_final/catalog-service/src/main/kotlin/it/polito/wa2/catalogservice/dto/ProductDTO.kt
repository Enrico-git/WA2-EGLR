package it.polito.wa2.catalogservice.dto

import java.math.BigDecimal
import java.sql.Timestamp

data class ProductDTO (
    val id: String?,
    val name: String?,
    val description: String?,
    val pictureUrl: String?,
    val category: String?,
    val price: BigDecimal?,
    val avgRating: Double?,
    val creationDate: Timestamp?,
    val comments: Set<String>?
)
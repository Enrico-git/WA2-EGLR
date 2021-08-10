package it.polito.wa2.warehouseservice.domain

import it.polito.wa2.warehouseservice.dto.ProductDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.sql.Timestamp

@Document(collection = "products")
data class Product (
        @Id
        val id: ObjectId?,
        var name: String,
        var description: String,
        var pictureUrl: String,
        var category: String,
        var price: BigDecimal,
        var avgRating: Double = 0.0,
        var creationDate: Timestamp,
        var comments: Set<ObjectId> = emptySet(),
        @Version
        val version: Long? = null
)

fun Product.toDTO() = ProductDTO(
        id = id?.toHexString(),
        name = name,
        description = description,
        pictureUrl = pictureUrl,
        category = category,
        price = price,
        avgRating = avgRating,
        creationDate = creationDate,
        comments = comments?.map{ it.toString() }?.toSet()
)
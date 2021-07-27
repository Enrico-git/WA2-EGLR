package it.polito.wa2.warehouseservice.domains

import it.polito.wa2.warehouseservice.dto.ProductDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.sql.Timestamp

@Document(collection = "products")
class Product (
        @Id
        val id: ObjectId?,
        val name: String,
        val description: String,
        val pictureUrl: String,
        val category: String,
        val price: BigDecimal,
        val avgRating: Double = 0.0,
        val creationDate: Timestamp,
        val comments: List<ObjectId>?,
        @Version
        val version: Long = Long.MIN_VALUE
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
        comments = comments?.map{ it.toString() }
)
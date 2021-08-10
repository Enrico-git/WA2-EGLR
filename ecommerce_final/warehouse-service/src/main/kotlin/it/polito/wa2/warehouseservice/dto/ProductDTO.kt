package it.polito.wa2.warehouseservice.dto

import it.polito.wa2.warehouseservice.constraintGroups.CreateOrReplaceProduct
import it.polito.wa2.warehouseservice.constraintGroups.CreateProduct
import java.math.BigDecimal
import java.sql.Timestamp
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Null

data class ProductDTO (
        @field:NotNull(message = "Id must not be null", groups = [CreateOrReplaceProduct::class])
        val id: String? = null,
        @field:NotNull(message = "Product name must not be null",
                groups = [CreateProduct::class, CreateOrReplaceProduct::class])
        val name: String?,
        @field:NotNull(message = "Product description must not be null",
                groups = [CreateProduct::class, CreateOrReplaceProduct::class])
        val description: String?,
        @field:NotNull(message = "Product picture's URL must not be null", groups = [CreateProduct::class, CreateOrReplaceProduct::class])
        val pictureUrl: String?,
        @field:NotNull(message = "Product category must not be null", groups = [CreateProduct::class, CreateOrReplaceProduct::class])
        val category: String?,
        @field:NotNull(message = "Product price must not be null", groups = [CreateProduct::class, CreateOrReplaceProduct::class])
        @field:Min(0, message = "Product price must not be null", groups = [CreateProduct::class, CreateOrReplaceProduct::class])
        val price: BigDecimal?,
        val avgRating: Double ? = null,
        val creationDate: Timestamp? = null,
        @field:NotNull(message = "Product comments must not be null",groups = [CreateOrReplaceProduct::class])
        val comments: Set<String>? = null
)
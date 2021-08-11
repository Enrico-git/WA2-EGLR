package it.polito.wa2.warehouseservice.dto

import it.polito.wa2.warehouseservice.constraintGroups.CreateOrReplaceWarehouse
import it.polito.wa2.warehouseservice.constraintGroups.PartialCreateOrUpdateWarehouse
import it.polito.wa2.warehouseservice.domain.ProductInfo
import org.bson.types.ObjectId
import javax.validation.constraints.Min


data class ProductInfoDTO(
        val id: String,
        @field:Min(0, message = "Alarm trigger quantity must be higher than 0", groups = [CreateOrReplaceWarehouse::class, PartialCreateOrUpdateWarehouse::class])
        val alarm: Int,
        @field:Min(0, message = "Product quantity must be higher than 0", groups = [CreateOrReplaceWarehouse::class, PartialCreateOrUpdateWarehouse::class])
        val quantity: Int
)

fun ProductInfoDTO.toEntity() = ProductInfo(
        productId = ObjectId(id),
        alarm = alarm,
        quantity = quantity
)

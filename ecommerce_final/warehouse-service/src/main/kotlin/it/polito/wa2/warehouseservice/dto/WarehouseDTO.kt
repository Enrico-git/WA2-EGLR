package it.polito.wa2.warehouseservice.dto

import it.polito.wa2.warehouseservice.constraintGroups.CreateOrReplaceWarehouse
import it.polito.wa2.warehouseservice.constraintGroups.PartialCreateOrUpdateWarehouse
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Null

data class WarehouseDTO (
        @field:NotNull(message = "Warehouse ID must not be null", groups = [CreateOrReplaceWarehouse::class])
        @field:Null(message = "Warehouse ID must not be null", groups = [PartialCreateOrUpdateWarehouse::class])
        val id: String?,
        @field:NotNull(message = "Warehouse products list must not be null", groups = [CreateOrReplaceWarehouse::class, PartialCreateOrUpdateWarehouse::class])
        @field:Valid
        val products: Set<ProductInfoDTO>?
)

//fun WarehouseDTO.toEntity() = Warehouse(
//        id = ObjectId(id),
//        products = products?.map{it.toEntity()}?.toMutableSet() ?: mutableSetOf()
//)
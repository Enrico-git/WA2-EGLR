package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface ProductService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductsByCategory(category: String): Flow<ProductDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductById(productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addProduct(productDTO: ProductDTO): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun modifyProduct(productDTO: ProductDTO, productID: String): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun removeProduct(productID: String): Boolean

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductPicture(productID: String): String

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun modifyProductPicture(newPicture: String, productID: String): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductWarehouse(productID: String): Flow<WarehouseDTO>
}
package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface ProductService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductsByCategory(category: String, pageable: Pageable): Flow<ProductDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductById(productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun partialUpdateProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addProduct(productDTO: ProductDTO): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun modifyProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteProduct(productID: ObjectId)

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductPicture(productID: ObjectId): String

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun modifyProductPicture(newPicture: String, productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductWarehouse(productID: ObjectId): Flow<WarehouseDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductComments(productID: ObjectId): Flow<CommentDTO>

}
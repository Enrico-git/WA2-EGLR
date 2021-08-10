package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.Warehouse
import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.dto.PictureDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toSet
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface ProductService {
    suspend fun getProductsByCategory(category: String?, pageable: Pageable): Flow<ProductDTO>

    suspend fun getProductById(productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addProduct(productDTO: ProductDTO): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun partialUpdateProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun modifyProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteProduct(productID: ObjectId)

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductPicture(productID: ObjectId): PictureDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun modifyProductPicture(pictureDTO: PictureDTO, productID: ObjectId): ProductDTO

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductWarehouses(productID: ObjectId): Flow<WarehouseDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductComments(productID: ObjectId): Flow<CommentDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun calculateRating(commentsIDs: Set<ObjectId>): Double
}
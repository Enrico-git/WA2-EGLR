package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.dto.PictureDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface ProductService {
    suspend fun getProducts(category: String?, page: Int?, size: Int?): Flow<ProductDTO>
    suspend fun getProduct(productID: ObjectId): ProductDTO
    suspend fun getProductPicture(productID: ObjectId): PictureDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun getProductWarehouses(productID: ObjectId): Flow<WarehouseDTO>
    suspend fun getProductComments(productID: ObjectId): Flow<CommentDTO>
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addProduct(productDTO: ProductDTO): ProductDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteProduct(productID: ObjectId)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updatePicture(productID: ObjectId, pictureDTO: PictureDTO): ProductDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun patchProduct(productID: ObjectId, productDTO: ProductDTO): ProductDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateProduct(productID: ObjectId, productDTO: ProductDTO): ProductDTO
}
package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.dto.PictureDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.prepost.PreAuthorize

interface ProductService {
    suspend fun getProducts(category: String?, page: Int?, size: Int?): Flow<ProductDTO>
    suspend fun getProduct(productID: String): ProductDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductPicture(productID: String): PictureDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductWarehouses(productID: String): Flow<WarehouseDTO>
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductComments(productID: String): Flow<CommentDTO>
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun addProduct(productDTO: ProductDTO): ProductDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteProduct(productID: String)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updatePicture(productID: String, pictureDTO: PictureDTO): ProductDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun patchProduct(productID: String, productDTO: ProductDTO): ProductDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateProduct(productID: String, productDTO: ProductDTO): ProductDTO
}
package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.dto.ProductDTO
import kotlinx.coroutines.flow.Flow

interface ProductService {
    suspend fun addProduct(productDTO: ProductDTO): ProductDTO
    suspend fun updateProduct(productID: Long, productDTO: ProductDTO): ProductDTO
    suspend fun getProductById(productID: Long): ProductDTO
    fun getAllProducts(): Flow<ProductDTO>
    fun getProductsByCategory(category: String): Flow<ProductDTO>
}
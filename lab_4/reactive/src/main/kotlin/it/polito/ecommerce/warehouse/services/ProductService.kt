package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.dto.ProductDTO
import kotlinx.coroutines.flow.Flow

interface ProductService {
    fun addProduct(product: ProductDTO): ProductDTO
    fun updateProduct(product: ProductDTO): ProductDTO
    suspend fun getProductById(productID: Long): ProductDTO
    fun getAllProducts(): Flow<ProductDTO>
    fun getProductsByCategory(category: String): Flow<ProductDTO>
}
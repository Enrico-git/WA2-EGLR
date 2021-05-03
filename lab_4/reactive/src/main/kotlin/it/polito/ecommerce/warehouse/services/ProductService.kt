package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.domain.Product
import it.polito.ecommerce.warehouse.dto.ProductDTO
import kotlinx.coroutines.flow.Flow

interface ProductService {
    suspend fun addProduct(productDTO: ProductDTO): ProductDTO
    suspend fun updateProduct(productID: Long, productDTO: ProductDTO): ProductDTO
    suspend fun getProductById(productID: Long): ProductDTO
    suspend fun getAllProducts(): Flow<ProductDTO>
    suspend fun getProductsByCategory(category: String): Flow<ProductDTO>
}
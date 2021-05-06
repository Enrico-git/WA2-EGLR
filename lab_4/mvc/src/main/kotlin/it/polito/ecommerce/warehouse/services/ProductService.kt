package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.dto.ProductDTO
import org.springframework.data.domain.Pageable

interface ProductService {
    fun addProduct(productDTO: ProductDTO): ProductDTO
    fun updateProduct(productID: Long, productDTO: ProductDTO): ProductDTO
    fun getProductById(productID: Long): ProductDTO
    fun getAllProducts(pageable: Pageable): List<ProductDTO>
    fun getProductsByCategory(category: String, pageable: Pageable): List<ProductDTO>
}
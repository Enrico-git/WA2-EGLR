package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.dto.toDTO
import it.polito.ecommerce.warehouse.exceptions.NotFoundException
import it.polito.ecommerce.warehouse.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional
@Service
class ProductServiceImpl(private val productRepository: ProductRepository) : ProductService{
    override fun addProduct(product: ProductDTO): ProductDTO {
        TODO("Not yet implemented")
    }

    override fun updateProduct(product: ProductDTO): ProductDTO {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productID: Long): ProductDTO {
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product was not found")
        return product.toDTO()
    }

    override fun getAllProducts(): Flow<ProductDTO> {
        TODO("Not yet implemented")
    }

    override fun getProductsByCategory(category: String): Flow<ProductDTO> {
        TODO("Not yet implemented")
    }
}
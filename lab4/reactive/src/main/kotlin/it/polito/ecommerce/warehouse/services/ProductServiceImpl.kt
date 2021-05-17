package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.domain.Product
import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.dto.toDTO
import it.polito.ecommerce.warehouse.exceptions.NotFoundException
import it.polito.ecommerce.warehouse.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository
): ProductService {

    override suspend fun addProduct(productDTO: ProductDTO): ProductDTO {
        val product = Product(productDTO.id,productDTO.name!!,productDTO.category!!,productDTO.price!!,productDTO.quantity)
        return productRepository.save(product).toDTO()
    }

    override suspend fun updateProduct(productID: Long, productDTO: ProductDTO): ProductDTO {
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("ProductID not valid")
        product.quantity += productDTO.quantity
        if(product.quantity < 0)
            throw IllegalArgumentException("Product quantity is not enough")
        return productRepository.save(product).toDTO()
    }

    override suspend fun getProductById(productID: Long): ProductDTO {
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product not found")
        return product.toDTO()
    }

    override fun getAllProducts(): Flow<ProductDTO> {
        return productRepository.findAll().map { it.toDTO() }
    }

    override fun getProductsByCategory(category: String): Flow<ProductDTO> {
        return productRepository.findAllByCategory(category).map{ it.toDTO() }
    }
}

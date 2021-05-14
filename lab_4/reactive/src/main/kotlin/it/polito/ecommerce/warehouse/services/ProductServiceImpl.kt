package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.domain.Product
import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.dto.toDTO
import it.polito.ecommerce.warehouse.exceptions.*
import it.polito.ecommerce.warehouse.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.cglib.proxy.Dispatcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository
): ProductService {
    override suspend fun addProduct(productDTO: ProductDTO): ProductDTO {

        val product = Product(
            id = productDTO.id,
            name = productDTO.name!!,
            category = productDTO.category!!,
            price = productDTO.price!!,
            quantity = productDTO.quantity
        )
        return productRepository.save(product).toDTO()
    }

    override suspend fun updateProduct(productID: Long, productDTO: ProductDTO): ProductDTO {
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")

        product.quantity += productDTO.quantity
        if(product.quantity < 0)
            throw IllegalArgumentException("Product quantity is not enough")

        return productRepository.save(product).toDTO()
    }

    override suspend fun getProductById(productID: Long): ProductDTO {
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        return product.toDTO()
    }

    override suspend fun getAllProducts(): Flow<ProductDTO> {
        return productRepository
            .findAll()
            .map { it.toDTO() }
    }

    override suspend fun getProductsByCategory(category: String): Flow<ProductDTO> {
        return productRepository
            .findAllByCategory(category)
            .map { it.toDTO() }
    }
}
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


@Transactional
@Service
class ProductServiceImpl(private val productRepository: ProductRepository) : ProductService{
    override suspend fun addProduct(productDTO: ProductDTO): ProductDTO {
        val product = Product(
            id = null,
            name = productDTO.name,
            category = productDTO.category,
            price = productDTO.price,
            quantity = productDTO.quantity
        )
        return productRepository.save(product).toDTO()
    }

    override suspend fun updateProduct(productID: Long, productDTO: ProductDTO): ProductDTO {
//        TODO why are we fetching the product before updating it? make custom query productRepository.update(productID, quantity)
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product not found")
        product.quantity += productDTO.quantity
        if(product.quantity < 0)
            throw IllegalArgumentException("Product quantity is not enough")

        return productRepository.save(product).toDTO()
    }

    override suspend fun getProductById(productID: Long): ProductDTO {
        println(productID)
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product was not found")
        return product.toDTO()
    }

    override fun getAllProducts(): Flow<ProductDTO> {
        return productRepository
            .findAll()
            .map{ it.toDTO() }
    }

    override fun getProductsByCategory(category: String): Flow<ProductDTO> {
        return productRepository
            .findAllByCategory(category)
            .map { it.toDTO() }
    }
}
package it.polito.ecommerce.warehouse.services

import it.polito.ecommerce.warehouse.domain.Product
import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.dto.toDTO
import it.polito.ecommerce.warehouse.repositories.ProductRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository
): ProductService {

    override fun addProduct(productDTO: ProductDTO): ProductDTO {
        val product = Product(productDTO.name!!, productDTO.category!!, productDTO.price!!, productDTO.quantity)
        return productRepository.save(product).toDTO()
    }

    override fun updateProduct(productID: Long, productDTO: ProductDTO): ProductDTO {
        val productOpt = productRepository.findById(productID)
        if( !productOpt.isPresent )
            throw IllegalArgumentException("Product ID not valid")
        val product = productOpt.get()
        product.quantity += productDTO.quantity
        if(product.quantity < 0)
            throw IllegalArgumentException("Product quantity is not enough")
        return productRepository.save(product).toDTO()
    }

    override fun getProductById(productID: Long): ProductDTO {
        val product = productRepository.findById(productID)
        if( !product.isPresent )
            throw IllegalArgumentException("Product ID not valid")
        return product.get().toDTO()
    }

    override fun getAllProducts(pageable: Pageable): List<ProductDTO> {
        return productRepository.findAllWithPageable(pageable).map { it.toDTO() }
    }

    override fun getProductsByCategory(category: String, pageable: Pageable): List<ProductDTO> {
        return productRepository.findAllByCategory(category,pageable).map{ it.toDTO() }
    }
}

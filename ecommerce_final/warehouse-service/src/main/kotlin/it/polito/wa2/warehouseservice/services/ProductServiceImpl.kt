package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domains.toDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.exceptions.NotFoundException
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
        private val productRepository: ProductRepository
): ProductService {
    override suspend fun getProductsByCategory(category: String, pageable: Pageable): Flow<ProductDTO> {
       return productRepository.findAllByCategory(category, pageable).map { it.toDTO() }
    }

    override suspend fun getProductById(productID: ObjectId): ProductDTO {
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product not found")
        return product!!.toDTO()
    }

    override suspend fun addProduct(productDTO: ProductDTO): ProductDTO {
        TODO("Not yet implemented")
    }

    override suspend fun modifyProduct(productDTO: ProductDTO, productID: String): ProductDTO {
        TODO("Not yet implemented")
    }

    override suspend fun removeProduct(productID: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getProductPicture(productID: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun modifyProductPicture(newPicture: String, productID: String): ProductDTO {
        TODO("Not yet implemented")
    }

    override suspend fun getProductWarehouse(productID: String): Flow<WarehouseDTO> {
        TODO("Not yet implemented")
    }
}
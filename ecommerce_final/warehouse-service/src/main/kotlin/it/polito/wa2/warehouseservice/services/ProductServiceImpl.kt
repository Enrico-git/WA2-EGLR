package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domains.toDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.UserDetailsDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.exceptions.NotFoundException
import it.polito.wa2.warehouseservice.exceptions.UnauthorizedException
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.types.ObjectId
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
        private val productRepository: ProductRepository
): ProductService {
    override suspend fun getProductsByCategory(category: String): Flow<ProductDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getProductById(productID: ObjectId): ProductDTO {
        println(productID)
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product not found")
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        println(user)
        if (! user.roles!!.contains("ADMIN"))
            throw UnauthorizedException("Forbidden")
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
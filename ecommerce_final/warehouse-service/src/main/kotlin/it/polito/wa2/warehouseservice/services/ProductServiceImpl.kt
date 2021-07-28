package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domains.Product
import it.polito.wa2.warehouseservice.domains.toDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.exceptions.*
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.sql.Timestamp
import kotlin.reflect.full.memberProperties

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
        return product.toDTO()
    }

    override suspend fun modifyOrInsertProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO {
//        val product = productRepository.existsById(productID)
//        return if( !product)  //add a new product
//            addProduct(productDTO)
//        else // update a product
//            modifyProduct(productDTO, productID)
        TODO("Not yet implemented")
    }
    override suspend fun partialUpdateProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO{
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        if(productDTO.avgRating != null)
            product.avgRating = productDTO.avgRating
        if(productDTO.category != null)
            product.category = productDTO.category
        if(productDTO.comments != null)
            product.comments = productDTO.comments.map{ObjectId(it)}.toSet()
        if(productDTO.description != null)
            product.description = productDTO.description
        if(productDTO.name != null)
            product.name = productDTO.name
        if(productDTO.pictureUrl != null)
            product.pictureUrl = productDTO.pictureUrl
        if(productDTO.price != null)
            product.price = productDTO.price

        return productRepository.save(product).toDTO()
    }

    override suspend fun addProduct(productDTO: ProductDTO): ProductDTO {
        val product = Product(
                id = null,
                name = productDTO.name!!,
                description = productDTO.description!!,
                pictureUrl = productDTO.pictureUrl!!,
                category = productDTO.category!!,
                price = productDTO.price!!,
                avgRating = productDTO.avgRating!!,
                creationDate = productDTO.creationDate!!,
                comments = productDTO.comments!!.map{ it -> ObjectId(it)}.toSet()
        )
        return productRepository.save(product).toDTO()
    }

    override suspend fun modifyProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productID: ObjectId) {
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        return productRepository.deleteById(productID)
    }

    override suspend fun getProductPicture(productID: ObjectId): String {
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product not found")
        return product.pictureUrl
    }

    override suspend fun modifyProductPicture(newPicture: String, productID: ObjectId): ProductDTO {
        var product = productRepository.findById(productID) ?: throw NotFoundException("Product not found")
        product.pictureUrl = newPicture
        return productRepository.save(product).toDTO()
    }

    override suspend fun getProductWarehouse(productID: ObjectId): Flow<WarehouseDTO> {
        TODO("Not yet implemented")
    }
}
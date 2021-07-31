package it.polito.wa2.warehouseservice.services


import it.polito.wa2.warehouseservice.domain.Product
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.exceptions.*
import it.polito.wa2.warehouseservice.repositories.CommentRepository
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import it.polito.wa2.warehouseservice.repositories.WarehouseRepository
import kotlinx.coroutines.flow.*
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import kotlin.IllegalArgumentException

@Service
@Transactional
class ProductServiceImpl(
        private val productRepository: ProductRepository,
        private val commentRepository: CommentRepository,
        private val warehouseRepository: WarehouseRepository
): ProductService {
    override suspend fun getProductsByCategory(category: String, pageable: Pageable): Flow<ProductDTO> {
        return productRepository.findAllByCategory(category, pageable).map { it.toDTO() }
    }

    override suspend fun getProductById(productID: ObjectId): ProductDTO {
        val product = productRepository.findById(productID) ?: throw NotFoundException("Product not found")
        return product.toDTO()
    }

    override suspend fun partialUpdateProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO{
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")

        product.avgRating = productDTO.avgRating ?: product.avgRating
        product.category = productDTO.category ?: product.category
        product.comments = productDTO.comments?.map{ObjectId(it)}?.toSet() ?: product.comments
        product.description = productDTO.description ?: product.description
        product.name = productDTO.name ?: product.name
        product.pictureUrl = productDTO.pictureUrl ?: product.pictureUrl
        product.price = productDTO.price ?: product.price
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
                creationDate = Timestamp(System.currentTimeMillis()),
                comments = productDTO.comments?.map{ObjectId(it)}?.toSet()
        )
       return productRepository.save(product).toDTO()
    }

    override suspend fun modifyProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO {
        val product = productRepository.existsById(productID)
        return if(product)
            partialUpdateProduct(productDTO, productID)
        else
            addProduct(productDTO)
    }

    override suspend fun deleteProduct(productID: ObjectId) {
        productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
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

    override suspend fun getProductWarehouses(productID: ObjectId): Flow<WarehouseDTO> {
        return  warehouseRepository.findAll()
//                .map{
//                    it.products
//                }

                .map { it ->
                    run {
                        println(it.products)
                        it.products?.filter {
                            it.productId == productID
                        }
                        println(it.products)
                    }
                    it.toDTO()
                }



//                .onEach { it -> it.products!!
//                        .filter{
//                            (it.productId == productID)
//                        }
//                }.map { it.toDTO() }
////                .forEach { it.products!!
//                            .filter{
//                                it.productId == productID
//                            }
//                }
        //return warehouses.map{ it.toDTO() }

    }

    override suspend fun getProductComments(productID: ObjectId): Flow<CommentDTO> {
        val commentsIds = productRepository.findById(productID)?.comments ?: throw IllegalArgumentException("Comments not found")
        return commentRepository.findAllById(commentsIds.asIterable()).map{ it.toDTO() }
    }
}
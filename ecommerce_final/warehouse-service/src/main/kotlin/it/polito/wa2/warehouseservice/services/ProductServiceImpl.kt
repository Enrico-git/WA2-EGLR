package it.polito.wa2.warehouseservice.services


import it.polito.wa2.warehouseservice.domain.Product
import it.polito.wa2.warehouseservice.domain.Warehouse
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.*
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



    override suspend fun getProducts(category: String?,  ids: Set<ObjectId>?, pageable: Pageable): Flow<ProductDTO> {
        val warehouses = warehouseRepository.findAll()
        return if(category != null)
            productRepository.findAllByCategory(category, pageable).map { productDTOWithAvailability(it, warehouses) }
        else if (ids != null)
            productRepository.findAllById(ids).map { productDTOWithAvailability(it, warehouses) }
        else
            productRepository.findAll(pageable).map { productDTOWithAvailability(it, warehouses) }
    }

    private suspend fun productDTOWithAvailability(product: Product, warehouses: Flow<Warehouse>): ProductDTO{
            val productDTO = product.toDTO()
            productDTO.availability = warehouses
                .map { wh -> wh.products.find { it.productId == product.id}?.quantity ?: 0}
                .reduce { acc, value -> acc+value }
        return productDTO
    }
    override suspend fun getProductById(productID: ObjectId): ProductDTO {
        val product = productRepository.findById(productID)?.toDTO() ?: throw NotFoundException("Product not found")
        product.availability = warehouseRepository.findWarehousesByProduct(productID)
            .map { wh -> wh.products.first { it.productId == productID}.quantity }
            .fold(0) { acc, value -> acc+value }
        return product
    }

    override suspend fun addProduct(productDTO: ProductDTO): ProductDTO {
        val product = Product(
            id = null,
            name = productDTO.name!!,
            description = productDTO.description!!,
            pictureUrl = productDTO.pictureUrl!!,
            category = productDTO.category!!,
            price = productDTO.price!!,
            avgRating = 0.0,
            creationDate = Timestamp(System.currentTimeMillis()),
            comments = emptySet()
        )
        return productRepository.save(product).toDTO()
    }

    override suspend fun partialUpdateProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO{
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")

        product.category = productDTO.category ?: product.category
        product.description = productDTO.description ?: product.description
        product.name = productDTO.name ?: product.name
        product.pictureUrl = productDTO.pictureUrl ?: product.pictureUrl
        product.price = productDTO.price ?: product.price
        if (productDTO.comments != null) {
            product.comments = productDTO.comments.map { ObjectId(it) }.toSet()
            product.avgRating = calculateRating(product.comments)
        }
        return productRepository.save(product).toDTO()
    }

    override suspend fun modifyProduct(productDTO: ProductDTO, productID: ObjectId): ProductDTO {
        val doesProductExist = productRepository.existsById(productID)
        return if(doesProductExist)
            partialUpdateProduct(productDTO, productID)
        else {
            val comments = productDTO.comments!!.map { ObjectId(it) }.toSet()
            return productRepository.save(
                Product(
                    id = productID,
                    name = productDTO.name!!,
                    description = productDTO.description!!,
                    pictureUrl = productDTO.pictureUrl!!,
                    category = productDTO.category!!,
                    price = productDTO.price!!,
                    creationDate = Timestamp(System.currentTimeMillis()),
                    comments = comments,
                    avgRating = calculateRating(comments)
                )
            ).toDTO()
        }
    }

    override suspend fun deleteProduct(productID: ObjectId) {
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        if(product.comments.isNotEmpty())
            commentRepository.deleteAllById(product.comments)
        val warehouses = warehouseRepository.findWarehousesByProduct(productID).map {
                wh ->
                    wh.products.removeIf { it.productId == productID }
                    wh
                }
        warehouseRepository.saveAll(warehouses).collect()
        productRepository.deleteById(productID)
    }

    override suspend fun getProductPicture(productID: ObjectId): PictureDTO {
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        return PictureDTO(product.pictureUrl)
    }

    override suspend fun modifyProductPicture(pictureDTO: PictureDTO, productID: ObjectId): ProductDTO {
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        product.pictureUrl = pictureDTO.pictureUrl
        return productRepository.save(product).toDTO()
    }

    override suspend fun getProductWarehouses(productID: ObjectId): Flow<WarehouseDTO> {
        return warehouseRepository.findWarehousesByProduct(productID).map { it.toDTO() }
    }

    override suspend fun calculateRating(commentsIDs: Set<ObjectId>): Double {
        return if (commentsIDs.isEmpty())
            0.0
        else {
            val comments = commentRepository.findAllById(commentsIDs).toSet()
            if (comments.size != commentsIDs.size)
                throw IllegalArgumentException("Invalid comments ids")
            comments.map{ it.stars }.reduce { acc, elem -> acc + elem }.div(comments.size).toDouble()
        }
    }
}


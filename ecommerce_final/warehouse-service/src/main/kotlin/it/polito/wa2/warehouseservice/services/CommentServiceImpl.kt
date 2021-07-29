package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.Comment
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.UserDetailsDTO
import it.polito.wa2.warehouseservice.repositories.CommentRepository
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.types.ObjectId
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.polito.wa2.warehouseservice.exceptions.*

@Service
@Transactional
class CommentServiceImpl(
        private val commentRepository: CommentRepository,
        private val productRepository: ProductRepository,
        private val productService: ProductService
): CommentService {
    override suspend fun addComment(productID: ObjectId, commentDTO: CommentDTO): CommentDTO {
        println("Service for addComment ok")
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        val comment = Comment(
            id = null,
            title = commentDTO.title!!,
            body = commentDTO.body!!,
            stars = commentDTO.stars!!,
            creationDate = commentDTO.creationDate!!,
            userId = user.username
        )
        println("new comment created")
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        println("product found")
        val commentsLength = product.comments?.size
        val newComment = commentRepository.save(comment)
        println("new comment added to the db")
        val productDTO = ProductDTO(
                id = null,
                name = null,
                description = null,
                pictureUrl = null,
                category = null,
                price = null,
                avgRating = (product?.avgRating?.times(commentsLength!!)+commentDTO.stars)/(commentsLength!!+1),
                creationDate = null,
                comments = product.comments!!.map{ it.toString() }.toSet() //+ newComment.id.toString()
        )
        println("new productDTo created")
        productService.partialUpdateProduct(productDTO, productID)
        return newComment.toDTO()
    }

    override suspend fun updateComment(productID: ObjectId, commentDTO: CommentDTO): CommentDTO {
        TODO("Not yet implemented")
    }

    override suspend fun deleteComment(commentId: ObjectId) {
        TODO("Not yet implemented")
    }
}
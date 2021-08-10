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
import java.sql.Timestamp

@Service
@Transactional
class CommentServiceImpl(
        private val commentRepository: CommentRepository,
        private val productRepository: ProductRepository,
        private val productService: ProductService
): CommentService {
    override suspend fun addComment(productID: ObjectId, commentDTO: CommentDTO): CommentDTO {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        val comment = Comment(
            id = null,
            title = commentDTO.title!!,
            body = commentDTO.body!!,
            stars = commentDTO.stars!!,
            creationDate = Timestamp(System.currentTimeMillis()),
            userId = user.username,
        )
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
//        val commentsLength = product.comments.size
        val newComment = commentRepository.save(comment)
//        val comments = product.comments.plus(newComment.id).map{it.toString()}.toSet()
        product.comments = product.comments.plus(newComment.id!!)
        product.avgRating = productService.calculateRating(product.comments)
//        val productDTO = ProductDTO(
//                id = null,
//                name = null,
//                description = null,
//                pictureUrl = null,
//                category = null,
//                price = null,
//                avgRating = (product.avgRating.times(commentsLength!!)+commentDTO.stars)/(commentsLength+1),
//                creationDate = null,
//                comments = comments
//        )
//        TODO this won't work because of security. User is Customer this method is only for admin
//        productService.partialUpdateProduct(productDTO, productID)
        productRepository.save(product)
        return newComment.toDTO()
    }

    override suspend fun updateComment(productID: ObjectId, commentId: ObjectId, commentDTO: CommentDTO): CommentDTO {
        val comment = commentRepository.findById(commentId) ?: throw IllegalArgumentException("Comment not found")
        val ratingChanged = comment.stars != commentDTO.stars
        comment.body = commentDTO.body!!
        comment.title = commentDTO.title!!
        comment.stars = commentDTO.stars!!
        val savedComment = commentRepository.save(comment).toDTO()
        if(ratingChanged){
            val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
            product.avgRating = productService.calculateRating(product.comments)
            productRepository.save(product)
        }
        return savedComment
    }

    override suspend fun deleteComment(productID: ObjectId, commentId: ObjectId) {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        if (! user.roles!!.contains("ADMIN") && user.id != commentId)
            throw UnauthorizedException("Nice try")
        if (!commentRepository.existsById(commentId))
            throw NotFoundException("Comment not found")
        commentRepository.deleteById(commentId)
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        product.comments = product.comments.minus(commentId)
        product.avgRating = productService.calculateRating(product.comments)
        productRepository.save(product)
    }

    override suspend fun getComment(commentId: ObjectId): CommentDTO {
        val comment = commentRepository.findById(commentId) ?: throw NotFoundException("Comment not found")
        return comment.toDTO()
    }
}
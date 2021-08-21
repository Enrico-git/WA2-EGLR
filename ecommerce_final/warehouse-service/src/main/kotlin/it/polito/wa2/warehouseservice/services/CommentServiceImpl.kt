package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.domain.Comment
import it.polito.wa2.warehouseservice.domain.toDTO
import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.dto.UserDetailsDTO
import it.polito.wa2.warehouseservice.exceptions.NotFoundException
import it.polito.wa2.warehouseservice.exceptions.UnauthorizedException
import it.polito.wa2.warehouseservice.repositories.CommentRepository
import it.polito.wa2.warehouseservice.repositories.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.types.ObjectId
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
            userId = user.id!!,
        )
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        val newComment = commentRepository.save(comment)
        product.comments = product.comments.plus(newComment.id!!)
        product.avgRating = productService.calculateRating(product.comments)
        productRepository.save(product)
        return newComment.toDTO()
    }

    override suspend fun updateComment(productID: ObjectId, commentId: ObjectId, commentDTO: CommentDTO): CommentDTO {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        val comment = commentRepository.findById(commentId) ?: throw IllegalArgumentException("Comment not found")
        if (! user.roles!!.contains("ADMIN") && user.id != comment.userId)
            throw UnauthorizedException("Nice try")
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
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        val comment = commentRepository.findById(commentId) ?: throw NotFoundException("Comment not found")
        if (! user.roles!!.contains("ADMIN") && user.id != comment.userId)
            throw UnauthorizedException("Nice try")
        commentRepository.delete(comment)
        product.comments = product.comments.minus(commentId)
        product.avgRating = productService.calculateRating(product.comments)
        productRepository.save(product)
    }

    override suspend fun getComments(productID: ObjectId): Flow<CommentDTO> {
        val product = productRepository.findById(productID)?: throw IllegalArgumentException("Product not found")
        return commentRepository.findAllById(product.comments).map{ it.toDTO() }
    }

    override suspend fun getComment(commentId: ObjectId): CommentDTO {
        val comment = commentRepository.findById(commentId) ?: throw NotFoundException("Comment not found")
        return comment.toDTO()
    }
}
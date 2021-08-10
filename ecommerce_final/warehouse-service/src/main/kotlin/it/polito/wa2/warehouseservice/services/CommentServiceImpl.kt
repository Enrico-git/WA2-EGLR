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
        val commentsLength = product.comments?.size
        val newComment = commentRepository.save(comment)
        val comments = product.comments?.plus(newComment.id)?.map{it.toString()}?.toSet()
        val productDTO = ProductDTO(
                id = null,
                name = null,
                description = null,
                pictureUrl = null,
                category = null,
                price = null,
                avgRating = (product.avgRating.times(commentsLength!!)+commentDTO.stars)/(commentsLength+1),
                creationDate = null,
                comments = comments
        )
//        TODO this won't work because of security. User is Customer this method is only for admin
        productService.partialUpdateProduct(productDTO, productID)
        return newComment.toDTO()
    }

    override suspend fun updateComment(productID: ObjectId, commentId: ObjectId, commentDTO: CommentDTO): CommentDTO {
        val comment = commentRepository.findById(commentId) ?: throw IllegalArgumentException("Comment not found")

        comment.body = commentDTO.body ?: comment.body
        comment.title = commentDTO.title ?: comment.title
        if(commentDTO.stars != null){
            val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
            val commentsLength = product.comments?.size
            val productDTO = ProductDTO(
                    id = null,
                    name = null,
                    description = null,
                    pictureUrl = null,
                    category = null,
                    price = null,
                    avgRating = ((product.avgRating.times(commentsLength!!) - comment.stars) + commentDTO.stars) / (commentsLength),
                    creationDate = null,
                    comments = product.comments!!.map{it.toString()}.toSet()
            )
            comment.stars = commentDTO.stars
            productService.partialUpdateProduct(productDTO, productID)
        }
        return commentRepository.save(comment).toDTO()
    }

    override suspend fun deleteComment(productID: ObjectId, commentId: ObjectId) {
        val comment = commentRepository.findById(commentId) ?: throw IllegalArgumentException("Comment not found")
        val product = productRepository.findById(productID) ?: throw IllegalArgumentException("Product not found")
        var commentsLength = product.comments?.size
        val comments = product.comments?.minus(commentId)?.map{it.toString()}?.toSet()
        commentsLength = product.comments?.size
        var avgRating = 0.0
        if(commentsLength != 0)
            avgRating =  (product.avgRating.times(commentsLength!!)-comment.stars)/(commentsLength)
        val productDTO = ProductDTO(
                id = null,
                name = null,
                description = null,
                pictureUrl = null,
                category = null,
                price = null,
                avgRating = avgRating,
                creationDate = null,
                comments = comments
        )
        productService.partialUpdateProduct(productDTO, productID)
        commentRepository.deleteById(commentId)
    }

    override suspend fun getComment(commentId: ObjectId): CommentDTO {
        val comment = commentRepository.findById(commentId) ?: throw NotFoundException("Comment not found")
        return comment.toDTO()
    }
}
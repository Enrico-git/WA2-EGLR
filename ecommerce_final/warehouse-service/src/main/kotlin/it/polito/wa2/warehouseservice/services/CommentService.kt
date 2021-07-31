package it.polito.wa2.warehouseservice.services

import it.polito.wa2.warehouseservice.dto.CommentDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize


interface CommentService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun addComment(productID: ObjectId, commentDTO: CommentDTO): CommentDTO

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun updateComment(productID: ObjectId, commentId: ObjectId, commentDTO: CommentDTO): CommentDTO

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun deleteComment(productID: ObjectId, commentId: ObjectId)

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getComment(commentId: ObjectId): CommentDTO

}
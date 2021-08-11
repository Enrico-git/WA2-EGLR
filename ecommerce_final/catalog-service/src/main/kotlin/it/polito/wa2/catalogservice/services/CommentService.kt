package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.CommentDTO
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize

interface CommentService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getComment(commentID: ObjectId): CommentDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun addComment(productID: ObjectId, commentDTO: CommentDTO): CommentDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun updateComment(productID: ObjectId, commentID: ObjectId, commentDTO: CommentDTO): CommentDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun deleteComment(productID: ObjectId, commentID: ObjectId)
}
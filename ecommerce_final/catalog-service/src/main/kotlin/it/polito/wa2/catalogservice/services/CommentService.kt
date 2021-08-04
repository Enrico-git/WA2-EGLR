package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.CommentDTO
import org.springframework.security.access.prepost.PreAuthorize

interface CommentService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getComment(commentID: String): CommentDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun addComment(productID: String, commentDTO: CommentDTO): CommentDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun updateComment(productID: String,commentID: String,commentDTO: CommentDTO): CommentDTO
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun deleteComment(productID: String, commentID: String)
}
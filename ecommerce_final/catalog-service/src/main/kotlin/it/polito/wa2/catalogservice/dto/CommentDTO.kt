package it.polito.wa2.catalogservice.dto

import java.sql.Timestamp

data class CommentDTO (
    val id: String?,
    val title: String?,
    val body: String?,
    val stars: Float?,
    val creationDate: Timestamp?,
    val userId: String?
)
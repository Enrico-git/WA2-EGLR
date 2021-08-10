package it.polito.wa2.warehouseservice.dto

import java.sql.Timestamp
import javax.validation.constraints.NotNull

data class CommentDTO (
        val id: String?,
        @field:NotNull(message = "Comment title cannot be null")
        val title: String?,
        @field:NotNull(message = "Comment body cannot be null")
        val body: String?,
        @field:NotNull(message = "Comment rating cannot be null")
        val stars: Float?,
        val creationDate: Timestamp?,
        val userId: String?
)
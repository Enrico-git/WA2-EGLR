package it.polito.wa2.catalogservice.dto

import java.sql.Timestamp
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class CommentDTO (
    val id: String?,
    @field:NotNull(message = "Comment title cannot be null")
    val title: String?,
    @field:NotNull(message = "Comment body cannot be null")
    val body: String?,
    @field:NotNull(message = "Comment rating cannot be null")
    @field:Min(0, message = "Number of stars can't be negative")
    @field:Max(5, message = "Number of stars can't be higher than 5")
    val stars: Float?,
    val creationDate: Timestamp?,
    val userId: String?
)
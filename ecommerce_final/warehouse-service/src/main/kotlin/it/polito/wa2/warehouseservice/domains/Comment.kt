package it.polito.wa2.warehouseservice.domains

import it.polito.wa2.warehouseservice.dto.CommentDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp

@Document(collection = "comments")
class Comment (
        @Id
        val id: ObjectId?,
        val title: String,
        val body: String,
        val stars: Float,
        val creationDate: Timestamp,
        @Version
        val version: Long = Long.MIN_VALUE
)

fun Comment.toDTO() = CommentDTO(
        id = id.toString(),
        title = title,
        body = body,
        stars = stars,
        creationDate = creationDate
)
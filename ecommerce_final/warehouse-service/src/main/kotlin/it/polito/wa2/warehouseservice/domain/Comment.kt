package it.polito.wa2.warehouseservice.domain

import it.polito.wa2.warehouseservice.dto.CommentDTO
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp

@Document(collection = "comments")
data class Comment (
        @Id
        val id: ObjectId?,
        var title: String,
        var body: String,
        var stars: Float,
        val creationDate: Timestamp,
        val userId: String?,
        @Version
        val version: Long? = null
)



fun Comment.toDTO() = CommentDTO(
        id = id.toString(),
        title = title,
        body = body,
        stars = stars,
        creationDate = creationDate,
        userId = userId
)
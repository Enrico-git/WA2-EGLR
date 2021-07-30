package it.polito.wa2.warehouseservice.repositories

import it.polito.wa2.warehouseservice.domain.Comment
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: CoroutineCrudRepository<Comment, ObjectId> {
    
}
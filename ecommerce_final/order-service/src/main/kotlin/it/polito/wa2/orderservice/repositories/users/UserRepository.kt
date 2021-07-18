package it.polito.wa2.orderservice.repositories.users

import it.polito.wa2.orderservice.domain.User
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : ReactiveMongoRepository<User, ObjectId> {
    @Query("{ '_id' : ?0 }")
    fun findById(userID: String): User
}
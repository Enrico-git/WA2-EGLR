package it.polito.wa2.catalogservice.repositories

import it.polito.wa2.catalogservice.domain.User
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CoroutineCrudRepository<User, ObjectId> {

    suspend fun findById(userID: String): User?

    suspend fun findByUsername(username: String): User?
}
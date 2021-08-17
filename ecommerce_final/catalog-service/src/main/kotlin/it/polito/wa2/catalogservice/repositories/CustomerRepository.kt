package it.polito.wa2.catalogservice.repositories

import it.polito.wa2.catalogservice.domain.Customer
import it.polito.wa2.catalogservice.domain.User
import org.bson.types.ObjectId
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : CoroutineCrudRepository<Customer, ObjectId>  {
    suspend fun findByUser(userID: ObjectId): Customer?
}
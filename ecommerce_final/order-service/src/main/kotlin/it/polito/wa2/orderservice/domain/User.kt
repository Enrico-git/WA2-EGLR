package it.polito.wa2.orderservice.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User (
    @Id
    val id: ObjectId? = null,
    val username: String,
    val password: String,
    val email: String,
    var isEnabled: Boolean = false,
    var roles: String,
    @Version
    val version: Long? = null
)
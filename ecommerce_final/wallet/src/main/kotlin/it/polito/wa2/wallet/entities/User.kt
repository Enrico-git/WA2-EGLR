package it.polito.wa2.wallet.entities

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class User(
    @Id
    val id: ObjectId?,
    val username: String,
    var password: String,
    val email: String,
    var isEnabled: Boolean = false,
    var roles: String
)

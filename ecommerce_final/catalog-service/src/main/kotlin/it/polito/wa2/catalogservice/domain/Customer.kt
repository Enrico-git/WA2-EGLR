package it.polito.wa2.catalogservice.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Customer(
    @Id
    val id: ObjectId? = null,
    var name: String = "",
    var surname: String = "",
    var address: String = "",
    var email: String = "",
    val user: User? //TODO when everything fixed remove "?"
) {
}

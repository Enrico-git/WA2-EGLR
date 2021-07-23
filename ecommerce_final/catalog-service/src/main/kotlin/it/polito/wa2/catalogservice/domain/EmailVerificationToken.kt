package it.polito.wa2.catalogservice.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.sql.Timestamp

@Document(collection = "email_verification_token")
class EmailVerificationToken(
    @Id
    val id: ObjectId? = null,
    val expiryDate: Timestamp,
    val token: String,
    val user: User,
) {
}
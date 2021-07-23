package it.polito.wa2.catalogservice.domain

import it.polito.wa2.catalogservice.common.Rolename
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
) {
    fun getRoles(): Set<Rolename> {
        return this.roles.split(",").map { it -> Rolename.valueOf(it) }.toSet()
    }

    fun addRole(role: Rolename) {
        this.roles = "$roles,$role"
    }

    fun removeRole(role: Rolename) {
        this.roles.split(",").filter { it != role.toString() }.reduce { acc, s -> "$acc,$s" }
    }
}
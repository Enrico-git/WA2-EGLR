package it.polito.ecommerce.domain

import it.polito.ecommerce.common.Rolename
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Index
import javax.persistence.Table
import javax.validation.constraints.Email

@Table(indexes = [Index(name = "Username", columnList = "username", unique = true)])
@Entity
class User(
    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    var password: String,

    @Column(unique = true, nullable = false)
    @Email
    val email: String,

    @Column(nullable = false, columnDefinition = "BIT default 0")
    var isEnabled: Boolean = false,

    @Column(nullable = false)
    var roles: String
) : EntityBase<Long>() {

    fun getRoles(): Set<Rolename> {
        return this.roles.split(",").map { Rolename.valueOf(it) }.toSet()
    }

    fun addRole(role: Rolename) {
        this.roles = "$roles,$role"
    }

    fun removeRole(role: Rolename) {
        this.roles = this.roles.split(",")
            .filter { it != role.toString() }
            .reduce { acc, string -> "$acc,$string" }
    }

    override fun toString(): String {
        return "${this.getId()}, ${this.username}, ${this.password}, ${this.isEnabled}, ${this.getRoles()}"
    }
}

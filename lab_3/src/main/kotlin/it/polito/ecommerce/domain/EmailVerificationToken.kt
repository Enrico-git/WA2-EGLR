package it.polito.ecommerce.domain

import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
class EmailVerificationToken(
    @Column(nullable = false)
    val expiryDate: Timestamp,

    @Column(nullable = false)
    val token: String,

    @OneToOne
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false, )
    val user: User
    ): EntityBase<Long>() {}

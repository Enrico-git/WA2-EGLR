package it.polito.ecommerce.domain

import javax.persistence.*
import javax.validation.constraints.Email

@Entity
class Customer(
    @Column(nullable = false)
    var name: String = "",
    @Column(nullable = false)
    var surname: String = "",
    @Column(nullable = false)
    var address: String = "",

    @Column(unique = true, nullable = false)
    @Email
    var email: String = "",

    @OneToOne
    @JoinColumn(name="user", referencedColumnName = "id", nullable = false)
    val user: User

): EntityBase<Long>() {
    @OneToMany(mappedBy = "customer", targetEntity = Wallet::class)
    val wallets: MutableSet<Wallet> = mutableSetOf<Wallet>()
}
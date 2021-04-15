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
    var email: String = ""
): EntityBase<Long>() {
    @OneToMany(mappedBy = "customer", targetEntity = Wallet::class)
    val wallets: MutableSet<Wallet> = mutableSetOf<Wallet>()
}
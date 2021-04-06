package it.polito.ecommerce.domain

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull

@Entity
class Customer(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false)
    val surname: String = "",
    @Column(nullable = false)
    val address: String = "",

    @Column(unique = true, nullable = false)
    @Email(message = "Email must be valid")
    val email: String = ""
) {
    @OneToMany(mappedBy = "customer", targetEntity = Wallet::class)
    val wallets: MutableList<Wallet> = mutableListOf<Wallet>()
}
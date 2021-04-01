package it.polito.ecommerce.domain

import javax.persistence.*
import javax.validation.constraints.Email

@Entity
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(nullable = false)
   val id:Int? = null,

    @Column(nullable = false)
    val name: String = "",

    @Column(nullable = false)
    val surname: String = "",

    @Column(nullable = false)
    val address: String = "",

    @Column(unique=true, nullable = false)
    @Email(message = "Email must be valid")
    val email: String = "") {

    @OneToMany(mappedBy=
        "customer", targetEntity=Wallet::class)
    val wallets: MutableList<Wallet> = mutableListOf<Wallet>()
}
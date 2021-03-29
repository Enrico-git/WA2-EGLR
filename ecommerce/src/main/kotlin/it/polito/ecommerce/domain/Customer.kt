package it.polito.ecommerce.domain

import javax.persistence.*

@Entity
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id:Int? = null
    @Column(nullable = false)
    val name: String = ""
    @Column(nullable = false)
    val surname: String = ""
    @Column(unique=true, nullable = false)
    val email: String = ""

    @OneToMany(mappedBy=
        "customer", targetEntity=Wallet::class)
    val wallets: MutableList<Wallet> = mutableListOf<Wallet>()

}
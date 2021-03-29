package it.polito.ecommerce.domain

import javax.persistence.*

@Entity
class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id:Int? = null

    @Column(nullable = false)
    val balance: Double = 0.0

    @ManyToOne
    @JoinColumn(name="customer", referencedColumnName = "id" )
    @Column(nullable = false)
    val customer: Customer? = null

    @OneToMany(mappedBy="sender", targetEntity=Transaction::class)
    val transactionsSent: MutableList<Transaction> = mutableListOf<Transaction>()

    @OneToMany(mappedBy="receiver", targetEntity=Transaction::class)
    val transactionsRecv: MutableList<Transaction> = mutableListOf<Transaction>()
}
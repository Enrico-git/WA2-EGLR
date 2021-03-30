package it.polito.ecommerce.domain

import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id: Int? = null

    @Min(value=0, message = "Balance cannot be negative")
    @Column(nullable = false, columnDefinition = "DOUBLE default 0")
    val balance: Double = 0.0

    @ManyToOne
    @JoinColumn(name="customer", referencedColumnName = "id", nullable = false)
    val customer: Customer? = null

    @OneToMany(mappedBy="sender", targetEntity=Transaction::class)
    val transactionsSent: MutableList<Transaction> = mutableListOf<Transaction>()

    @OneToMany(mappedBy="receiver", targetEntity=Transaction::class)
    val transactionsRecv: MutableList<Transaction> = mutableListOf<Transaction>()
}

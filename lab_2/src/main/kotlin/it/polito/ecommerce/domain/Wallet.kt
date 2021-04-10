package it.polito.ecommerce.domain

import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Wallet(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2) default 0")
    @Min(value = 0, message = "Balance cannot be negative")
    var balance: BigDecimal = BigDecimal(0.0),

    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    val customer: Customer,
) {
    @OneToMany(mappedBy = "sender", targetEntity = Transaction::class)
    val transactionsSent: MutableList<Transaction> = mutableListOf<Transaction>()

    @OneToMany(mappedBy = "receiver", targetEntity = Transaction::class)
    val transactionsReceived: MutableList<Transaction> = mutableListOf<Transaction>()

}
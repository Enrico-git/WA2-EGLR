package it.polito.ecommerce.domain

import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Wallet(
    @Column(nullable = false, columnDefinition = "DECIMAL(15,2) default 0")
    @Min(value = 0)
    var balance: BigDecimal = BigDecimal(0.0),

    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    val customer: Customer,
    @Version
    val version: Long = 0

) : EntityBase<Long>() {
    @OneToMany(mappedBy = "sender", targetEntity = Transaction::class)
    val transactionsSent: MutableSet<Transaction> = mutableSetOf<Transaction>()

    @OneToMany(mappedBy = "receiver", targetEntity = Transaction::class)
    val transactionsReceived: MutableSet<Transaction> = mutableSetOf<Transaction>()

}
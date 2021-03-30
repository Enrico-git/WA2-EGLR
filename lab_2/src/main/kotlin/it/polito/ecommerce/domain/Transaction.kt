package it.polito.ecommerce.domain

import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Integer? = null
    @Column(nullable = false)
    val timestamp: Timestamp? = null

    @ManyToOne
    @JoinColumn(name="sender", referencedColumnName = "id", nullable = false)
    val sender: Wallet? = null

    @ManyToOne
    @JoinColumn(name="receiver", referencedColumnName = "id", nullable = false)
    val receiver: Wallet? = null

    @Column(nullable = false, columnDefinition = "DOUBLE default 0")
    @Min(value = 0, message = "Transaction amount cannot be negative")
    val amount: Double = 0.0
}
package it.polito.ecommerce.domain

import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id:Int? = null

    @Column(nullable = false)
    val timestamp:Timestamp? = null

    @ManyToOne
    @JoinColumn(name="sender", referencedColumnName = "id", nullable = false)
    var sender: Wallet? = null

    @ManyToOne
    @JoinColumn(name="receiver", referencedColumnName = "id", nullable = false)
    var receiver: Wallet? = null

    @Min(value=0, message = "Balance cannot be negative")
    @Column(nullable = false)
    val amount: Double =  0.0
}
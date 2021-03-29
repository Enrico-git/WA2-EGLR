package it.polito.ecommerce.domain

import java.sql.Timestamp
import javax.persistence.*

@Entity
class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id:Int? = null

    @Column(nullable = false)
    val timestamp:Timestamp? = null

    @ManyToOne
    @JoinColumn(name="sender", referencedColumnName = "id")
    var sender: Wallet? = null

    @ManyToOne
    @JoinColumn(name="receiver", referencedColumnName = "id")
    var receiver: Wallet? = null

    @Column(nullable = false)
    val amount: Double =  0.0
}
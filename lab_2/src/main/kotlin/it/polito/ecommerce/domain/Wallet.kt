package it.polito.ecommerce.domain

import it.polito.ecommerce.dto.WalletDTO
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Wallet(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,

    @Column(nullable = false, columnDefinition = "DOUBLE default 0")
    @Min(value = 0, message = "Balance cannot be negative")
    var balance: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "id", nullable = false)
    val customer: Customer? = null,
) {
    @OneToMany(mappedBy = "sender", targetEntity = Transaction::class)
    val transactionsSent: MutableList<Transaction> = mutableListOf<Transaction>()

    @OneToMany(mappedBy = "receiver", targetEntity = Transaction::class)
    val transactionsReceived: MutableList<Transaction> = mutableListOf<Transaction>()

    fun toDTO(): WalletDTO {
        return WalletDTO(
            id = id!!,
            balance = balance,
            customer = customer?.id!!
        )
    }
}
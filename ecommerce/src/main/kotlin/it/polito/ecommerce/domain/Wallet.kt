package it.polito.ecommerce.domain

import it.polito.ecommerce.dto.WalletDTO
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Wallet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id:Int? = null,

    @Min(value=0, message = "Balance cannot be negative")
    @Column(nullable = false, columnDefinition = "DOUBLE default 0")
    var balance: Double = 0.0,

    @ManyToOne
    @JoinColumn(name="customer", referencedColumnName = "id", nullable = false)
    val customer: Customer
) {
    @OneToMany(mappedBy="sender", targetEntity=Transaction::class)
    val transactionsSent: MutableList<Transaction> = mutableListOf<Transaction>()

    @OneToMany(mappedBy="receiver", targetEntity=Transaction::class)
    val transactionsRecv: MutableList<Transaction> = mutableListOf<Transaction>()

    fun toDTO() = WalletDTO(id = id!!,
        balance = balance,
        customer = customer.name + " " + customer.surname
    )
}
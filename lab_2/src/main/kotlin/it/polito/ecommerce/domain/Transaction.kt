package it.polito.ecommerce.domain

import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Transaction (
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int? = null,
    @Column(nullable = false)
    val timestamp: Timestamp,

    @ManyToOne
    @JoinColumn(name="sender", referencedColumnName = "id", nullable = false)
    val sender: Wallet,

    @ManyToOne
    @JoinColumn(name="receiver", referencedColumnName = "id", nullable = false)
    val receiver: Wallet,

    @Column(nullable = false, columnDefinition = "DOUBLE default 0")
    @Min(value = 0, message = "Transaction amount cannot be negative")
    val amount: Double = 0.0
) {

    fun toDTO(): TransactionDTO {
        return TransactionDTO(
            id = id!!,
            timestamp = timestamp,
            sender = sender?.id!!,
            receiver = receiver?.id!!,
            amount = amount
            )
    }
}

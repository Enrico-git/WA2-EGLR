package it.polito.ecommerce.domain

import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import java.math.BigDecimal
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Transaction (
    @Column(nullable = false)
    val timestamp: Timestamp,

    @ManyToOne
    @JoinColumn(name="sender", referencedColumnName = "id", nullable = false)
    val sender: Wallet,

    @ManyToOne
    @JoinColumn(name="receiver", referencedColumnName = "id", nullable = false)
    val receiver: Wallet,

    @Column(nullable = false, columnDefinition = "DECIMAL(15,2) default 0")
    @Min(value = 0, message = "Transaction amount cannot be negative")
    val amount: BigDecimal = BigDecimal(0.0)
): EntityBase<Long>()
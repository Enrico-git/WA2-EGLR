package it.polito.ecommerce.domain

import java.math.BigDecimal
import java.sql.Timestamp
import javax.persistence.*
import javax.validation.constraints.Min

@Entity
class Transaction(
    @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false)
  val id:Int? = null,

    @Column(nullable = false)
    val timestamp:Timestamp,

    @ManyToOne
    @JoinColumn(name="sender", referencedColumnName = "id", nullable = false)
    var sender: Wallet,

    @ManyToOne
    @JoinColumn(name="receiver", referencedColumnName = "id", nullable = false)
    var receiver: Wallet,

    @Min(value=0, message = "Balance cannot be negative")
    @Column(nullable = false, columnDefinition = "DECIMAL default 0")
    val amount: BigDecimal =  BigDecimal(0.0)
)


package it.polito.ecommerce.domain

import it.polito.ecommerce.dto.TransactionDTO
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
    @Column(nullable = false)
    val amount: Double =  0.0
){
  fun toDTO() = TransactionDTO(id!!, timestamp,
    sender.customer.name + " " + sender.customer.surname,
    receiver.customer.name + " " + receiver.customer.surname,
    amount  )
}
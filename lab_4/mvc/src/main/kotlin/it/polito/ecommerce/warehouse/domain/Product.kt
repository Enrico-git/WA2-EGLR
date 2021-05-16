package it.polito.ecommerce.warehouse.domain

import it.polito.ecommerce.domain.EntityBase
import org.hibernate.annotations.Type
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Product(
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val category: String,
    @Column(nullable = false)
    var price: BigDecimal,
    @Column(nullable = false)
    var quantity: Long,
    @Column(nullable = false)
    @Version
    val version: Long = Long.MIN_VALUE
) : EntityBase<Long>()
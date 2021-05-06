package it.polito.ecommerce.warehouse.domain

import it.polito.ecommerce.domain.EntityBase
import org.hibernate.annotations.Type
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import javax.persistence.*

@Entity
class Product (
    @Column
    val name: String,
    @Column
    val category: String,
    @Column
    var price: BigDecimal,
    @Column
    var quantity: Long,
    @Column
    @Version
    val version: Long = Long.MIN_VALUE
) : EntityBase<Long>()
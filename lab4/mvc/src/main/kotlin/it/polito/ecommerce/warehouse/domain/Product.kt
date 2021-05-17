package it.polito.ecommerce.warehouse.domain

import it.polito.ecommerce.domain.EntityBase
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Version

@Entity
class Product (
    @Column
    val name: String,
    @Column
    val category: String,
    @Column
    val price: BigDecimal,
    @Column
    var quantity: Long,
    @Column
    @Version
    var version: Long = Long.MIN_VALUE
): EntityBase<Long>() {
}
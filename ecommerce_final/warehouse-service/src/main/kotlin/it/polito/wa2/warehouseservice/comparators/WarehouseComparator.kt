package it.polito.wa2.warehouseservice.comparators

import it.polito.wa2.warehouseservice.domain.Warehouse
import org.bson.types.ObjectId

class WarehouseComparator(val productId: ObjectId): Comparator<Warehouse>{
    override fun compare(p0: Warehouse, p1: Warehouse): Int {
        val product1 = p0.products.find { it.productId == productId }
        val product2 = p1.products.find { it.productId == productId }

        return if(product1!!.quantity >= product2!!.quantity)
            1
        else
            -1
    }
}
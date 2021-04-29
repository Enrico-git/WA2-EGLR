package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Customer
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CustomerRepository: CrudRepository<Customer, Long> {
    @Query("SELECT c FROM User u, Customer c WHERE u.id=c.user and u.username=?1 and c.id=?2")
    fun findByUserAndID(username: String, customerID: Long): Optional<Customer>
}
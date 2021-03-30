package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.Customer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository:CrudRepository<Customer, Int?> {

}

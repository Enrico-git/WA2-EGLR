package it.polito.ecommerce.repositories

import it.polito.ecommerce.domain.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<User, Long?> {
    @Query("select u from User u where u.username=?1")
    fun findByUsername(username: String): Optional<User>
}
//package it.polito.ecommerce.services
//
//import it.polito.ecommerce.dto.UserDetailsDTO
//import org.springframework.security.core.userdetails.UserDetails
//
//interface UserDetailsService {
//    fun loadUserByUsername(username: String?): UserDetails
//    fun addUser(userDetailsDTO: UserDetailsDTO): UserDetailsDTO
//    fun addRole(username: String, role: String): UserDetailsDTO
//    fun removeRole(username: String, role:String): UserDetailsDTO
//    fun enableUser(username: String) : UserDetailsDTO
//    fun disableUser(username: String) : UserDetailsDTO
//}
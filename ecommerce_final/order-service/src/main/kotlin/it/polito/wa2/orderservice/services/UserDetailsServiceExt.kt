package it.polito.wa2.orderservice.services

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService


interface UserDetailsService {
    suspend fun findByUsername(username: String): UserDetails
}
package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.domain.toDTO
import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.dto.UserDetailsDTO
import it.polito.wa2.orderservice.repositories.orders.OrderRepository
import it.polito.wa2.orderservice.repositories.users.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class OrderServiceImpl(
    private val repository: OrderRepository,
    private val userRepository: UserRepository,
    private val logger: Logger
): OrderService {
    override suspend fun getOrders(pageable: Pageable): Flow<OrderDTO> {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        return repository.findAllByUsername(user.username, pageable).map { it.toDTO() }
    }
}
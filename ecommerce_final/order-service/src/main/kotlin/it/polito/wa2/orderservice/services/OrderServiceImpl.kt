package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.domain.User
import it.polito.wa2.orderservice.domain.toDTO
import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.repositories.orders.OrderRepository
import it.polito.wa2.orderservice.repositories.users.UserRepository
import kotlinx.coroutines.flow.*
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderServiceImpl(
    private val repository: OrderRepository,
    private val userRepository: UserRepository
): OrderService {
    override suspend fun getOrders(pageable: Pageable): Flow<OrderDTO> {
//        TODO TAKE ID FROM PRINCIPAL AFTER AUTH
        val userID = 1L
        return repository.findAllByUserID(userID, pageable).map { it.toDTO() }
    }
}
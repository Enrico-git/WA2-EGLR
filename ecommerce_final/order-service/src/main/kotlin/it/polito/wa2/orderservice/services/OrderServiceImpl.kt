package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.domain.Order
import it.polito.wa2.orderservice.domain.toDTO
import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.dto.UserDetailsDTO
import it.polito.wa2.orderservice.exceptions.InvalidOperationException
import it.polito.wa2.orderservice.exceptions.NotFoundException
import it.polito.wa2.orderservice.exceptions.UnauthorizedException
import it.polito.wa2.orderservice.repositories.orders.OrderRepository
import it.polito.wa2.orderservice.repositories.users.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.logging.Logger

@Service
@Transactional
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val logger: Logger
): OrderService {
    override suspend fun getOrders(pageable: Pageable): Flow<OrderDTO> {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        return orderRepository.findAllByBuyer(user.username, pageable).map { it.toDTO() }
    }

    override suspend fun getOrderByID(orderID: ObjectId): OrderDTO {
        val order = orderRepository.findById(orderID) ?: throw NotFoundException("Order was not found")
        return order.toDTO()
    }

    override suspend fun updateOrder(orderID: ObjectId, orderDTO: OrderDTO): OrderDTO {
        val order = orderRepository.findById(orderID) ?: throw IllegalArgumentException("Order not found")
        order.status = orderDTO.status
        return orderRepository.save(order).toDTO()
    }

    override suspend fun deleteOrder(orderID: ObjectId) {
        val order = orderRepository.findById(orderID) ?: throw IllegalArgumentException("Order not found")
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        if ( ! user.roles!!.contains("ADMIN") && user.username != order.buyer )
            throw UnauthorizedException("Forbidden")
        if (order.status != OrderStatus.ISSUED)
            throw InvalidOperationException("You cannot cancel the order anymore")
        order.status = OrderStatus.CANCELED
        orderRepository.save(order)
    }
}
package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.common.EmailType
import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.domain.Order
import it.polito.wa2.orderservice.domain.toDTO
import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.dto.SagaDTO
import it.polito.wa2.orderservice.dto.UserDetailsDTO
import it.polito.wa2.orderservice.exceptions.InvalidOperationException
import it.polito.wa2.orderservice.exceptions.NotFoundException
import it.polito.wa2.orderservice.exceptions.UnauthorizedException
import it.polito.wa2.orderservice.orchestrator.OrchestratorImpl
import it.polito.wa2.orderservice.repositories.OrderRepository
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.logging.Logger

@Service
@Transactional
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val orchestrator: OrchestratorImpl,
    private val mailService: MailService
): OrderService {
    override suspend fun getOrders(pageable: Pageable): Flow<OrderDTO> {
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        return orderRepository.findAllByBuyer(user.id!!, pageable).map { it.toDTO() }
    }

    override suspend fun getOrderByID(orderID: ObjectId): OrderDTO {
        val order = orderRepository.findById(orderID) ?: throw NotFoundException("Order was not found")
        val user = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication.principal as UserDetailsDTO
        if ( ! user.roles!!.contains("ADMIN") && user.id != order.buyer )
            throw UnauthorizedException("Forbidden")
        return order.toDTO()
    }

    override suspend fun updateOrderStatus(orderID: ObjectId, orderDTO: OrderDTO): OrderDTO {
        val order = orderRepository.findById(orderID) ?: throw IllegalArgumentException("Order not found")
        order.status = orderDTO.status!!
        mailService.notifyCustomer(orderDTO.email!!, "Order ${order.id} Notification", order.id.toString(), EmailType.UPDATE, order.status )
        mailService.notifyAdmin("Order ${order.id} Notification", order.id.toString(), EmailType.UPDATE, order.status )
        return orderRepository.save(order).toDTO()
    }

    override suspend fun deleteOrder(orderID: ObjectId, orderDTO: OrderDTO) {
        val order = orderRepository.findById(orderID) ?: throw IllegalArgumentException("Order not found")
        val auth = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication
        val user = auth.principal as UserDetailsDTO
        val token = auth.credentials as String
        if ( ! user.roles!!.contains("ADMIN") && user.id != order.buyer )
            throw UnauthorizedException("Forbidden")
        if (order.status != OrderStatus.ISSUED)
            throw InvalidOperationException("You cannot cancel the order anymore")
        CoroutineScope(Dispatchers.Default).launch {
            orchestrator.createSaga(SagaDTO(
                id = order.id.toString(),
                type = "delete_order",
                customerEmail = orderDTO.email!!,
                amount = order.products.map{ BigDecimal(it.amount).multiply(it.price) }
                    .reduce{acc, elem -> acc+elem },
                auth = token
            ))
        }
    }

    override suspend fun createOrder(orderDTO: OrderDTO): OrderDTO {
        val auth = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication
        val user = auth.principal as UserDetailsDTO
        val token = auth.credentials as String
        val order = Order(
            id = null,
            buyer = user.id!!,
            products = orderDTO.products!!,
            status = OrderStatus.PENDING,
            deliveryAddress = orderDTO.deliveryAddress!!
        )

        val storedOrder = orderRepository.save(order)
        CoroutineScope(Dispatchers.Default).launch {
            orchestrator.createSaga(SagaDTO(
                id = storedOrder.id.toString(),
                type = "new_order",
                customerEmail = orderDTO.email!!,
                shippingAddress = orderDTO.deliveryAddress,
                amount = order.products.map{ BigDecimal(it.amount)
                    .multiply(it.price)}
                    .reduce{acc, elem -> acc+elem },
                products = order.products.map { it.toDTO() }.toHashSet(),
                auth = token
            ))
        }

        return storedOrder.toDTO()
    }

    override suspend fun updateOrderOnSagaEnding(sm: StateMachineImpl, status: OrderStatus, mailType: EmailType) {
        val order = orderRepository.findById(ObjectId(sm.id)) ?: throw IllegalArgumentException("Order not found")
        order.status = status
        orderRepository.save(order)
        mailService.notifyCustomer(sm.customerEmail, "ORDER ${sm.id} NOTIFICATION", sm.id,  mailType)
        mailService.notifyAdmin("ORDER ${sm.id} NOTIFICATION", sm.id,  mailType)
    }
}
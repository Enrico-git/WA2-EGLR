package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.common.EmailType
import it.polito.wa2.orderservice.common.OrderStatus
import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize

interface OrderService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getOrders(pageable: Pageable): Flow<OrderDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getOrderByID(orderID: ObjectId): OrderDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateOrderStatus(orderID: ObjectId, orderDTO: OrderDTO): OrderDTO

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun deleteOrder(orderID: ObjectId, orderDTO: OrderDTO)

    @PreAuthorize("hasAuthority(\"CUSTOMER\")")
    suspend fun createOrder(orderDTO: OrderDTO): OrderDTO

    suspend fun updateOrderOnSagaEnding(sm: StateMachineImpl, status: OrderStatus, mailType: EmailType)
}
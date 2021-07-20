package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.dto.OrderDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Flux

interface OrderService {
    suspend fun getOrders(pageable: Pageable): Flow<OrderDTO>
    suspend fun getOrderByID(orderID: ObjectId): OrderDTO
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateOrder(orderID: ObjectId, orderDTO: OrderDTO): OrderDTO
    suspend fun deleteOrder(orderID: ObjectId)
}
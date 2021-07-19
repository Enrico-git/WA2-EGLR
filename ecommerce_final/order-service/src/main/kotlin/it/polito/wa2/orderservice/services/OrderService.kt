package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.dto.OrderDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux

interface OrderService {
    suspend fun getOrders(pageable: Pageable): Flow<OrderDTO>
    suspend fun getOrderByID(orderID: ObjectId): OrderDTO
}
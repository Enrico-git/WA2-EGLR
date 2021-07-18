package it.polito.wa2.orderservice.services

import it.polito.wa2.orderservice.dto.OrderDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux

interface OrderService {
//    TODO add auth
    suspend fun getOrders(pageable: Pageable): Flow<OrderDTO>

}
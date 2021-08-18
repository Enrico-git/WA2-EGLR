package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.OrderDTO
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.security.access.prepost.PreAuthorize
import reactor.core.publisher.Mono


interface OrderService {
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getOrders(page: Int?, size: Int?): Flow<OrderDTO>

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getOrder(orderID: ObjectId): OrderDTO

    @PreAuthorize("hasAuthority(\"CUSTOMER\")")
    suspend fun newOrder(orderDTO: OrderDTO): OrderDTO

    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun deleteOrder(orderID: ObjectId, orderDTO: OrderDTO)

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateOrder(orderID: ObjectId, orderDTO: OrderDTO): OrderDTO
}
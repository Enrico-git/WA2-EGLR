package it.polito.wa2.orderservice.controllers

import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.services.OrderService
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {

    /**
     * API endpoint to retrieve all the orders of the users
     * @param pageable the pagination details
     * @return flow of OrderDTO
     */
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getOrders(pageable: Pageable): Flow<OrderDTO> = orderService.getOrders(pageable)
}
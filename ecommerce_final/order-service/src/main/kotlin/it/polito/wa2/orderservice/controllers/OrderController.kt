package it.polito.wa2.orderservice.controllers

import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.services.OrderService
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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

    /**
     * API endpoint to retrieve the order by its ID
     * @param orderID the ID of the order
     * @return OrderDTO
     */
    @GetMapping("/{orderID}")
    suspend fun getOrderByID(@PathVariable orderID: ObjectId): OrderDTO = orderService.getOrderByID(orderID)
}
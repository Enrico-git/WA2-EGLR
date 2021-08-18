package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.constraintGroups.CreateOrder
import it.polito.wa2.catalogservice.constraintGroups.DeleteOrder
import it.polito.wa2.catalogservice.constraintGroups.UpdateOrder
import it.polito.wa2.catalogservice.dto.OrderDTO
import it.polito.wa2.catalogservice.services.OrderService
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService
) {
    /**
     * API endpoint to retrieve all the orders of the users
     * @param page the page number of the pagination
     * @param size the size of the pagination
     * @return the stream of orders object
     */
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOrders(@RequestParam page: Int?, @RequestParam size: Int?): Flow<OrderDTO> {
        return orderService.getOrders(page,size)
    }

    /**
     * API endpoint to retrieve the order by its ID
     * @param orderID the ID of the order
     * @return the order object
     */
    @GetMapping("/{orderID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOrder(@PathVariable orderID: ObjectId): OrderDTO {
        return orderService.getOrder(orderID)
    }

    /**
     * API endpoint to create a new order
     * @param orderDTO the details of the order
     * @return the created order details
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newOrder(@RequestBody @Validated(CreateOrder::class) orderDTO: OrderDTO): OrderDTO {
        return orderService.newOrder(orderDTO)
    }

    /**
     * API endpoint to delete the order by its ID
     * @param orderID the id of the order
     */
    @DeleteMapping("/{orderID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteOrder(@PathVariable orderID: ObjectId, @RequestBody @Validated(DeleteOrder::class) orderDTO: OrderDTO) {
        return orderService.deleteOrder(orderID, orderDTO)
    }

    /**
     * API endpoint to update the order by its ID
     * @param orderID the id of the order
     * @param orderDTO JSON object with only the new status field
     * @return the updated object
     */
    @PatchMapping("/{orderID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateOrder(@PathVariable orderID: ObjectId, @RequestBody @Validated(UpdateOrder::class) orderDTO: OrderDTO): OrderDTO {
        return orderService.updateOrder(orderID, orderDTO)
    }
}
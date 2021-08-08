package it.polito.wa2.orderservice.controllers

import it.polito.wa2.orderservice.constraintGroups.CreateOrder
import it.polito.wa2.orderservice.constraintGroups.DeleteOrder
import it.polito.wa2.orderservice.constraintGroups.UpdateOrder
import it.polito.wa2.orderservice.dto.OrderDTO
import it.polito.wa2.orderservice.services.OrderService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.UncategorizedMongoDbException
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
     * @param pageable the pagination details
     * @return the stream of orders object
     */
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getOrders(pageable: Pageable): Flow<OrderDTO> = orderService.getOrders(pageable)

    /**
     * API endpoint to retrieve the order by its ID
     * @param orderID the ID of the order
     * @return the order object
     */
    @GetMapping("/{orderID}")
    suspend fun getOrderByID(@PathVariable orderID: ObjectId): OrderDTO = orderService.getOrderByID(orderID)

    /**
     * API endpoint to update the order by its ID
     * @param orderID the id of the order
     * @param orderDTO JSON object with only the new status field
     * @return the updated object
     */
     @PatchMapping("/{orderID}")
     @ResponseStatus(HttpStatus.CREATED)
     suspend fun updateOrderByID(@PathVariable orderID: ObjectId, @RequestBody @Validated(UpdateOrder::class) orderDTO: OrderDTO) : OrderDTO {
        var counter = 5
        while (counter-- > 0){
            try {
                return orderService.updateOrderStatus(orderID, orderDTO)
            }
            catch(e: UncategorizedMongoDbException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Order")
    }

    /**
     * API endpoint to delete the order by its ID
     * @param orderID the id of the order
     */
     @DeleteMapping("/{orderID}")
     @ResponseStatus(HttpStatus.NO_CONTENT)
     suspend fun deleteOrderByID(@PathVariable orderID: ObjectId, @RequestBody @Validated(DeleteOrder::class) orderDTO: OrderDTO) {
        var counter = 5
        while (counter-- > 0){
            try {
                return orderService.deleteOrder(orderID, orderDTO)
            }
            catch(e: UncategorizedMongoDbException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Order")
    }

    /**
     * API endpoint to create a new order
     * @param orderDTO the details of the order
     * @return the created order details
     */
     @PostMapping("")
     @ResponseStatus(HttpStatus.CREATED)
     suspend fun createOrder(@RequestBody @Validated(CreateOrder::class) orderDTO: OrderDTO): OrderDTO {
        return orderService.createOrder(orderDTO)
    }
}
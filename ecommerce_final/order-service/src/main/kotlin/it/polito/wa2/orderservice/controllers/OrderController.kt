package it.polito.wa2.orderservice.controllers

import com.mongodb.MongoCommandException
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
     suspend fun updateOrderByID(@PathVariable orderID: ObjectId, @RequestBody orderDTO: OrderDTO) : OrderDTO {
        var counter = 5
//        println(orderDTO)
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
     suspend fun deleteOrderByID(@PathVariable orderID: ObjectId, @RequestBody orderDTO: OrderDTO) {
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
     suspend fun createOrder(@RequestBody orderDTO: OrderDTO): OrderDTO {
        return orderService.createOrder(orderDTO)
    }
}
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

    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOrders(): Flow<OrderDTO> {
        return orderService.getOrders()
    }

    //RETRIEVE AN ORDER GIVEN ITS ID
    @GetMapping("/{orderID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOrder(@PathVariable orderID: ObjectId): OrderDTO {
        return orderService.getOrder(orderID)
    }

    //CREATE A NEW ORDER
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newOrder(@RequestBody @Validated(CreateOrder::class) orderDTO: OrderDTO): OrderDTO {
        return orderService.newOrder(orderDTO)
    }

    //DELETE AN ORDER GIVEN ITS ID (IF POSSIBLE)
    @DeleteMapping("/{orderID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteOrder(@PathVariable orderID: ObjectId, @RequestBody @Validated(DeleteOrder::class) orderDTO: OrderDTO) {
        return orderService.deleteOrder(orderID, orderDTO)
    }
//
    //UPDATE AN ORDER GIVEN ITS ID
    @PatchMapping("/{orderID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateOrder(@PathVariable orderID: ObjectId, @RequestBody @Validated(UpdateOrder::class) orderDTO: OrderDTO): OrderDTO {
        return orderService.updateOrder(orderID, orderDTO)
    }
}
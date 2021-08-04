package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.OrderDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import it.polito.wa2.catalogservice.services.OrderService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

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
    suspend fun newOrder(@RequestBody orderDTO: OrderDTO): OrderDTO {
        return orderService.newOrder(orderDTO)
    }

    //DELETE AN ORDER GIVEN ITS ID (IF POSSIBLE)
    @DeleteMapping("/{orderID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteOrder(@PathVariable orderID: ObjectId, @RequestBody orderDTO: OrderDTO) {
        return orderService.deleteOrder(orderID, orderDTO)
    }
//
    //UPDATE AN ORDER GIVEN ITS ID
    @PatchMapping("/{orderID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateOrder(@PathVariable orderID: ObjectId, @RequestBody orderDTO: OrderDTO): OrderDTO {
        return orderService.updateOrder(orderID, orderDTO)
    }
}
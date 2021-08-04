package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.OrderDTO
import it.polito.wa2.catalogservice.exceptions.NotFoundException
import it.polito.wa2.catalogservice.exceptions.UnauthorizedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitSingle
import org.bson.json.JsonObject
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import java.util.*
import java.util.function.IntPredicate
import java.util.function.Predicate

@Service
class OrderServiceImpl(
    @Qualifier("order-service-client") private val loadBalancedWebClientBuilder: WebClient.Builder
) : OrderService{
    val serviceURL = "http://order-service"

    val client: WebClient = loadBalancedWebClientBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    override suspend fun getOrders(): Flow<OrderDTO> {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .get()
            .uri("$serviceURL/orders")
            .accept(MediaType.APPLICATION_NDJSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .bodyToFlow()
    }

    override suspend fun getOrder(orderID: ObjectId): OrderDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .get()
            .uri("$serviceURL/orders/$orderID")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.NOT_FOUND }) { throw NotFoundException("Order not found") }
            .onStatus(Predicate { it == HttpStatus.FORBIDDEN }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun newOrder(orderDTO: OrderDTO): OrderDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .post()
            .uri("$serviceURL/orders/")
            .bodyValue(orderDTO)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun deleteOrder(orderID: ObjectId, orderDTO: OrderDTO) {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        client
            .method(HttpMethod.DELETE)
            .uri("$serviceURL/orders/$orderID")
            .bodyValue  (
                orderDTO
            )
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.FORBIDDEN }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw RuntimeException("The order does not exist") }
            .awaitBodilessEntity()
        return
    }

    override suspend fun updateOrder(orderID: ObjectId, orderDTO: OrderDTO): OrderDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .patch()
            .uri("$serviceURL/orders/$orderID")
            .bodyValue(orderDTO)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw RuntimeException("The order does not exist") }
            .awaitBody()
    }
}
package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.OrderDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
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
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

@RestController
@RequestMapping("/orders")
class OrderController(
    @Qualifier("order-service-client") private val loadBalancedWebClientBuilder: WebClient.Builder
) {
    val serviceURL = "http://order-service"

    val client: WebClient = loadBalancedWebClientBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    //RETRIEVE ALL ORDERS OF A CUSTOMER
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getOrders(): Flow<OrderDTO> {

        return ReactiveSecurityContextHolder.getContext().flatMapMany {
            val token = it.authentication.credentials as String
            return@flatMapMany client.get().uri("$serviceURL/orders").accept(MediaType.APPLICATION_NDJSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .bodyToFlux<OrderDTO>()
        }.asFlow()
    }

    //RETRIEVE AN ORDER GIVEN ITS ID
    @GetMapping("/{orderID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getOrder(@PathVariable orderID: ObjectId): Mono<OrderDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders/$orderID")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue("")

        //Preparing a Request: define the Headers
        val responseSpec: WebClient.ResponseSpec = headersSpec.header(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE
        )
            .accept(MediaType.APPLICATION_NDJSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .ifNoneMatch("*")
            .ifModifiedSince(ZonedDateTime.now())
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        return headersSpec.exchangeToMono { response: ClientResponse ->
            return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
        }
    }

    //CREATE A NEW ORDER
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newOrder(@RequestBody orderDTO: OrderDTO): Mono<OrderDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(orderDTO)

        //Preparing a Request: define the Headers
        val responseSpec: WebClient.ResponseSpec = headersSpec.header(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE
        )
            .accept(MediaType.APPLICATION_NDJSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .ifNoneMatch("*")
            .ifModifiedSince(ZonedDateTime.now())
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        return headersSpec.exchangeToMono { response: ClientResponse ->
            if (response.statusCode() == HttpStatus.CREATED) {
                return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
            }
        }
    }

    //DELETE AN ORDER GIVEN ITS ID (IF POSSIBLE)
    @DeleteMapping("/{orderID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteOrder(@PathVariable orderID: ObjectId, @RequestBody orderDTO: OrderDTO): Mono<String> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.DELETE)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders/$orderID")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(orderDTO)

        //Preparing a Request: define the Headers
        val responseSpec: WebClient.ResponseSpec = headersSpec.header(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE
        )
            .accept(MediaType.APPLICATION_NDJSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .ifNoneMatch("*")
            .ifModifiedSince(ZonedDateTime.now())
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response TODO see if the request starts, because this endpoint returns nothing
        return headersSpec.exchangeToMono { response: ClientResponse ->
            return@exchangeToMono response.bodyToMono(String::class.java)
        }
    }

    //UPDATE AN ORDER GIVEN ITS ID
    @PatchMapping("/{orderID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateOrder(@PathVariable orderID: ObjectId, @RequestBody orderDTO: OrderDTO): Mono<OrderDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PATCH)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders/$orderID")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(orderDTO)

        //Preparing a Request: define the Headers
        val responseSpec: WebClient.ResponseSpec = headersSpec.header(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE
        )
            .accept(MediaType.APPLICATION_NDJSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .ifNoneMatch("*")
            .ifModifiedSince(ZonedDateTime.now())
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        return headersSpec.exchangeToMono { response: ClientResponse ->
            if (response.statusCode() == HttpStatus.CREATED) {
                return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
            }
        }
    }
}
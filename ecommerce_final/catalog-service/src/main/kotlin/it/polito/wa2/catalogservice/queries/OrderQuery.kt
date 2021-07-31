package it.polito.wa2.catalogservice.queries

import it.polito.wa2.catalogservice.dto.OrderDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.reactive.function.client.exchangeToFlow
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

//I DELETE SUSPEND BECAUSE IT GAVES ME AN ERROR IN OrderWiring.kt, CHECK IF IT WORKS
@Component
class OrderQuery(
    @Qualifier("order-service-client") private val loadBalancedWebClientBuilder: WebClient.Builder
) {

    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
//    val client: WebClient = WebClient.builder()
//        .baseUrl("http://localhost:6379")
//        .defaultUriVariables(Collections.singletonMap("url", "http://localhost:6379"))
    val client = loadBalancedWebClientBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", "http://order-service"))
        .build()

    //RETRIEVE ALL ORDERS OF A CUSTOMER
    @ResponseStatus(HttpStatus.OK)
    fun orders(token: String): Flow<OrderDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders")

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
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        //TODO see if with this logic, if there's an exception it will be thrown
        return headersSpec.exchangeToFlow { response: ClientResponse ->
            return@exchangeToFlow response.bodyToFlow<OrderDTO>()
        }
    }

    //RETRIEVE AN ORDER GIVEN ITS ID
    @ResponseStatus(HttpStatus.OK)
    fun order(orderID: String, token: String): Mono<OrderDTO> {
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
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        return headersSpec.exchangeToMono { response: ClientResponse ->
            return@exchangeToMono response.bodyToMono(OrderDTO::class.java)
        }
    }
}
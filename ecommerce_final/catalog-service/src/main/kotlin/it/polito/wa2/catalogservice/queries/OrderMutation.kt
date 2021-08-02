package it.polito.wa2.catalogservice.queries

import it.polito.wa2.catalogservice.domain.Delivery
import it.polito.wa2.catalogservice.domain.Product
import it.polito.wa2.catalogservice.dto.CreateWalletDTO
import it.polito.wa2.catalogservice.dto.OrderDTO
import it.polito.wa2.catalogservice.dto.TransactionDTO
import it.polito.wa2.catalogservice.dto.WalletDTO
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

//I DELETE SUSPEND BECAUSE IT GAVES ME AN ERROR IN OrderWiring.kt, CHECK IF IT WORKS
@Component
class OrderMutation(
    @Qualifier("order-service-client") private val loadBalancedWebClientBuilder: WebClient.Builder
) {

    //Create a WebClient instance FOR ORDER SERVICE
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
//    val client: WebClient = WebClient.builder()
//        .baseUrl("http://localhost:6379")
//        .defaultUriVariables(Collections.singletonMap("url", "http://localhost:6379"))
    val client = loadBalancedWebClientBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", "http://order-service"))
        .build()

    //CREATE A NEW ORDER
    @ResponseStatus(HttpStatus.CREATED)
    fun newOrder(buyerID: String, products: Set<Product>, delivery: String,
                         email: String, token: String): Mono<OrderDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders")

        //Preparing a Request: define the Body
        var orderDTO = OrderDTO(buyer= ObjectId(buyerID),products=products,delivery= Delivery(delivery,null),
            email=email,id=null,status=null)
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(orderDTO)

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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteOrder(orderID: String, token: String): Mono<String> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.DELETE)

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

        //Get a response TODO see if the request starts, because this endpoint returns nothing
        return headersSpec.exchangeToMono { response: ClientResponse ->
            return@exchangeToMono response.bodyToMono(String::class.java)
        }
    }

    //UPDATE AN ORDER GIVEN ITS ID
    @ResponseStatus(HttpStatus.CREATED)
    fun updateOrder(orderID: String, products: Set<Product>, delivery: String?, email: String?,
                            buyerID: String?, token: String): Mono<OrderDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PATCH)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders/$orderID")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var deliveryObj: Delivery? = null
        if(delivery!=null)
            deliveryObj = Delivery(delivery,null)
        var orderDTO = OrderDTO(buyer= ObjectId(buyerID),products=products,delivery=deliveryObj,
            email=email,id=null,status=null)
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(orderDTO)

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
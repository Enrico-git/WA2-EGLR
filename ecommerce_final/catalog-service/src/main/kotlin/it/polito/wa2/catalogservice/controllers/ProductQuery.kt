package it.polito.wa2.catalogservice.controllers

import com.expediagroup.graphql.spring.operations.Query
import it.polito.wa2.catalogservice.domain.Delivery
import it.polito.wa2.catalogservice.dto.OrderDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
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

//TODO see if with this logic, if there's an exception it will be thrown
@Component
class ProductQuery(): Query {
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    //TODO fix number of port of warehouse service
    val client: WebClient = WebClient.builder()
        .baseUrl("http://localhost:8200")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8200"))
        .build()

    //RETRIEVE ALL THE PRODUCTS, OR ALL THE PRODUCTS OF A GIVEN CATEGORY
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @ResponseStatus(HttpStatus.OK)
    suspend fun products(category: String?): Flow<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var url: String = ""
        if(category!=null)
            url += "?category=$category"
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products$url")

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
        return headersSpec.exchangeToFlow { response: ClientResponse ->
            return@exchangeToFlow response.bodyToFlow<ProductDTO>()
        }
    }

    //RETRIEVE INFO ABOUT A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @ResponseStatus(HttpStatus.OK)
    suspend fun product(productID: String): Mono<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID")

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
            .retrieve()

        //Get a response
        return headersSpec.exchangeToMono { response: ClientResponse ->
            return@exchangeToMono response.bodyToMono(ProductDTO::class.java)
        }
    }

    //DELETE A PRODUCT GIVEN ITS ID
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteProduct(productID: String, token: String): Unit {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.DELETE)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID")

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
        val response = headersSpec.retrieve()
    }

    //RETRIEVE THE PICTURE URL OF A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @ResponseStatus(HttpStatus.OK)
    suspend fun picture(productID: String): Mono<String> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets/$productID/picture")

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
            return@exchangeToMono response.bodyToMono(String::class.java)
        }
    }

    //UPDATE THE PICTURE OF A PRODUCT GIVEN ITS ID AND THE NEW PICTURE
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updatePicture(productID: String, picture: String, token: String): Mono<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets/$productID/picture")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(picture)

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
            return@exchangeToMono response.bodyToMono(ProductDTO::class.java)
        }
    }

    //RETRIEVE THE LIST OF WAREHOUSES THAT CONTAIN A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @ResponseStatus(HttpStatus.OK)
    suspend fun productWarehouses(productID: String): Flow<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID/warehouses")

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
            .retrieve()

        //Get a response
        return headersSpec.exchangeToFlow { response: ClientResponse ->
            return@exchangeToFlow response.bodyToFlow<WarehouseDTO>()
        }
    }

    //PARTIALLY UPDATE A PRODUCT GIVEN ITS ID
    //TODO fix how to pass ProductDTO (which fields?)
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun patchProduct(productID: String, token: String): Mono<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PATCH)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID")

        //Preparing a Request: define the Body
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
            return@exchangeToMono response.bodyToMono(ProductDTO::class.java)
        }
    }

    //UPDATE A PRODUCT GIVEN ITS ID, OR ADD A NEW ONE IF THE ID DOES NOT EXIST
    //TODO fix how to pass ProductDTO (which fields?)
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateProduct(productID: String, token: String): Mono<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PUT)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID")

        //Preparing a Request: define the Body
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
            return@exchangeToMono response.bodyToMono(ProductDTO::class.java)
        }
    }
}

/*
* if (response.statusCode() == HttpStatus.CREATED) {
*
* } else if (response.statusCode().is4xxClientError) {
*   return@exchangeToMono response.bodyToMono(ProductDTO::class.java)
  } else {
    return@exchangeToMono response.bodyToMono(ProductDTO::class.java)
  }*/
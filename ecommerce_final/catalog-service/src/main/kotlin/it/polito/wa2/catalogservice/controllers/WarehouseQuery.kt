package it.polito.wa2.catalogservice.controllers

import com.expediagroup.graphql.spring.operations.Query
import it.polito.wa2.catalogservice.dto.ProductInfoDTO
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

@Component
class WarehouseQuery(): Query {

    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    //TODO fix number of port of warehouse service
    val client: WebClient = WebClient.builder()
        .baseUrl("http://localhost:8200")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8200"))
        .build()

    //RETRIEVE THE LIST OF WAREHOUSES
    @ResponseStatus(HttpStatus.OK)
    suspend fun warehouses(token: String): Flow<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses")

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
        return headersSpec.exchangeToFlow { response: ClientResponse ->
            if (response.statusCode() == HttpStatus.OK) {
                return@exchangeToFlow response.bodyToFlow<WarehouseDTO>()
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToFlow response.bodyToFlow()
            } else {
                return@exchangeToFlow response.bodyToFlow()
            }
        }
    }

    //RETRIEVE INFO ABOUT A WAREHOUSE GIVEN ITS ID
    @ResponseStatus(HttpStatus.OK)
    suspend fun warehouse(warehouseID: String, token: String): Mono<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses/$warehouseID")

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
            if (response.statusCode() == HttpStatus.OK) {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            }
        }
    }

    //DELETE A WAREHOUSE GIVEN ITS ID, IF POSSIBLE
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteWarehouse(warehouseID: String, token: String): Unit {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.DELETE)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses/$warehouseID")

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

    //CREATE A NEW WAREHOUSE WITH A LIST OF PRODUCTS
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newWarehouse(products: Set<ProductInfoDTO>, token: String): Mono<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var warehouseDTO = WarehouseDTO(null,products)
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(warehouseDTO)

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
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            }
        }
    }

    //PARTIALLY UPDATE A WAREHOUSE GIVEN ITS ID
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun patchProduct(warehouseID: String, products: Set<ProductInfoDTO>,
                             token: String): Mono<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PATCH)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses/$warehouseID")

        //Preparing a Request: define the Body
        var warehouseDTO = WarehouseDTO(warehouseID, products)
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(warehouseDTO)

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
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            }
        }
    }

    //UPDATE A WAREHOUSE GIVEN ITS ID, OR ADD A NEW ONE IF THE ID DOES NOT EXIST
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateProduct(warehouseID: String, products: Set<ProductInfoDTO>,
                              token: String): Mono<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PUT)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses/$warehouseID")

        //Preparing a Request: define the Body
        var warehouseDTO = WarehouseDTO(warehouseID, products)
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(warehouseDTO)

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
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
            }
        }
    }
}
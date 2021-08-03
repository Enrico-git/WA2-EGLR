package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

@RestController
@RequestMapping("/warehouses")
class WarehouseController {
    val serviceURL = "http://localhost:8200"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = WebClient.builder()
        .baseUrl(serviceURL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    //RETRIEVE THE LIST OF WAREHOUSES
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getWarehouses(): Flow<WarehouseDTO> {

        return ReactiveSecurityContextHolder.getContext().flatMapMany {
            val token = it.authentication.credentials as String
            return@flatMapMany client.get().uri("$serviceURL/warehouses").accept(MediaType.APPLICATION_NDJSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .bodyToFlux<WarehouseDTO>()
        }.asFlow()
    }

    //RETRIEVE INFO ABOUT A WAREHOUSE GIVEN ITS ID
    @GetMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getWarehouse(@PathVariable warehouseID: String): Mono<WarehouseDTO> {
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
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        return headersSpec.exchangeToMono { response: ClientResponse ->
            return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
        }
    }

    //DELETE A WAREHOUSE GIVEN ITS ID, IF POSSIBLE
    @DeleteMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteWarehouse(@PathVariable warehouseID: String) {
        //TODO see if it works
        ReactiveSecurityContextHolder.getContext().map {
            val token = it.authentication.credentials as String
            return@map client.delete().uri("$serviceURL/warehouses/$warehouseID")
                .accept(MediaType.APPLICATION_NDJSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
        }
        /*
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
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        headersSpec.retrieve()
         */
    }

    //CREATE A NEW WAREHOUSE WITH A LIST OF PRODUCTS
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newWarehouse(@RequestBody warehouseDTO: WarehouseDTO): Mono<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(warehouseDTO)

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
            return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
        }
    }

    //PARTIALLY UPDATE A WAREHOUSE GIVEN ITS ID
    @PatchMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun patchWarehouse(@PathVariable warehouseID: String, @RequestBody warehouseDTO: WarehouseDTO): Mono<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PATCH)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses/$warehouseID")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(warehouseDTO)

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
            return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
        }
    }

    //UPDATE A WAREHOUSE GIVEN ITS ID, OR ADD A NEW ONE IF THE ID DOES NOT EXIST
    @PutMapping("/{warehouseID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateWarehouse(@PathVariable warehouseID: String, @RequestBody warehouseDTO: WarehouseDTO): Mono<WarehouseDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PUT)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/warehouses/$warehouseID")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(warehouseDTO)

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
            return@exchangeToMono response.bodyToMono(WarehouseDTO::class.java)
        }
    }
}
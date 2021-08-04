package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.dto.PictureDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

@RestController
@RequestMapping("/products")
class ProductController {
    val serviceURL = "http://localhost:8200"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = WebClient.builder()
        .baseUrl(serviceURL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    //RETRIEVE ALL THE PRODUCTS, OR ALL THE PRODUCTS OF A GIVEN CATEGORY
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProducts(@RequestParam category: String, pageable: Pageable): Flow<ProductDTO> {
        var queryParam: String = ""
        if(category!=null)
            queryParam+="?category=$category"
        return ReactiveSecurityContextHolder.getContext().flatMapMany {
            return@flatMapMany client.get().uri("$serviceURL/products$queryParam")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux<ProductDTO>()
        }.asFlow()
    }

    //RETRIEVE INFO ABOUT A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProduct(@PathVariable productID: String): Mono<ProductDTO> {

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

    //RETRIEVE THE PICTURE URL OF A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}/picture")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductPicture(@PathVariable productID: String): Mono<PictureDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID/picture")

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
            return@exchangeToMono response.bodyToMono(PictureDTO::class.java)
        }
    }

    //RETRIEVE THE LIST OF WAREHOUSES THAT CONTAIN A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}/warehouses")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductWarehouses(@PathVariable productID: String): Flow<WarehouseDTO> {

        return ReactiveSecurityContextHolder.getContext().flatMapMany {
            //val token = it.authentication.credentials as String
            return@flatMapMany client.get().uri("$serviceURL/products/$productID/warehouses")
                .accept(MediaType.APPLICATION_NDJSON)
                //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .bodyToFlux<WarehouseDTO>()
        }.asFlow()
    }

    //RETRIEVE THE LIST OF WAREHOUSES THAT CONTAIN A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}/comments")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority(\"ADMIN\") or hasAuthority(\"CUSTOMER\")")
    suspend fun getProductComments(@PathVariable productID: String): Flow<CommentDTO> {

        return ReactiveSecurityContextHolder.getContext().flatMapMany {
            //val token = it.authentication.credentials as String
            return@flatMapMany client.get().uri("$serviceURL/products/$productID/comments")
                .accept(MediaType.APPLICATION_NDJSON)
                //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .bodyToFlux<CommentDTO>()
        }.asFlow()
    }

    //DELETE A PRODUCT GIVEN ITS ID
    @DeleteMapping("/{productID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteProduct(@PathVariable productID: String) {
        //TODO see if it works
        ReactiveSecurityContextHolder.getContext().map {
            val token = it.authentication.credentials as String
            return@map client.delete().uri("$serviceURL/products/$productID")
                .accept(MediaType.APPLICATION_NDJSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
        }
        /*
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
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        headersSpec.retrieve()
         */
    }

    //UPDATE THE PICTURE OF A PRODUCT GIVEN ITS ID AND THE NEW PICTURE
    @PostMapping("/{productID}/picture")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updatePicture(@PathVariable productID: String, @RequestBody pictureDTO: PictureDTO): Mono<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID/picture")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(pictureDTO)

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

    //PARTIALLY UPDATE A PRODUCT GIVEN ITS ID
    @PatchMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    fun patchProduct(@PathVariable productID: String, @RequestBody productDTO: ProductDTO): Mono<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PATCH)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(productDTO)

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

    //UPDATE A PRODUCT GIVEN ITS ID, OR ADD A NEW ONE IF THE ID DOES NOT EXIST
    @PutMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun updateProduct(@PathVariable productID: String, @RequestBody productDTO: ProductDTO): Mono<ProductDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PUT)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(productDTO)

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
}

/*
* //specify an HTTP method of a request by invoking method(HttpMethod method)
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
 */
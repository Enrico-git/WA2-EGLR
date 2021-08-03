package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

@RestController
@RequestMapping("/comments")
class CommentController {
    val serviceURL = "http://localhost:8200"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = WebClient.builder()
        .baseUrl(serviceURL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    //RETRIEVE INFO ABOUT A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{commentID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getComment(@PathVariable commentID: String): Mono<CommentDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/comments/$commentID")

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
            return@exchangeToMono response.bodyToMono(CommentDTO::class.java)
        }
    }

    //CREATE A NEW WAREHOUSE WITH A LIST OF PRODUCTS
    @PostMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addComment(@PathVariable productID: String, @RequestBody commentDTO: CommentDTO): Mono<CommentDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/comments")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(commentDTO)

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
            return@exchangeToMono response.bodyToMono(CommentDTO::class.java)
        }
    }

    //UPDATE A PRODUCT GIVEN ITS ID, OR ADD A NEW ONE IF THE ID DOES NOT EXIST
    @PutMapping("/{productID}/{commentID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateComment(@PathVariable productID: String,
                              @PathVariable commentID: String,
                              @RequestBody commentDTO: CommentDTO): Mono<CommentDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.PUT)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/comments/$productID/$commentID")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(commentDTO)

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
            return@exchangeToMono response.bodyToMono(CommentDTO::class.java)
        }
    }

    //DELETE A PRODUCT GIVEN ITS ID
    @DeleteMapping("/{productID}/{commentID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteComment(@PathVariable productID: String, @PathVariable commentID: String) {
        //TODO see if it works
        ReactiveSecurityContextHolder.getContext().map {
            val token = it.authentication.credentials as String
            return@map client.delete().uri("$serviceURL/comments/$productID/$commentID")
                .accept(MediaType.APPLICATION_NDJSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
        }
        /*
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.DELETE)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/products/$productID/$commentID")

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
}
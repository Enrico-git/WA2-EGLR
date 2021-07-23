package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.services.UserDetailsService
import org.springframework.stereotype.Component
import com.expediagroup.graphql.spring.operations.Query
import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.dto.OrderDTO
import it.polito.wa2.catalogservice.dto.RegistrationDTO
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import kotlinx.coroutines.flow.Flow
import org.springframework.http.*
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.reactive.function.client.exchangeToFlow
import java.nio.charset.StandardCharsets
import java.util.*
import java.time.ZonedDateTime

@Component
//@Controller TODO try if graphql needs the Annotation @Controller
class CatalogQuery(private val userService: UserDetailsService): Query {

    suspend fun createUser(registrationDTO: RegistrationDTO): ResponseEntity<UserDetailsDTO> {
        return ResponseEntity(userService.registerUser(registrationDTO), HttpStatus.CREATED)
    }

    suspend fun signIn(loginDTO: LoginDTO): ResponseEntity<LoginDTO> {
        return ResponseEntity(userService.authAndCreateToken(loginDTO), HttpStatus.OK)
    }

    suspend fun registrationConfirm(token: String): ResponseEntity<Unit> {
        return ResponseEntity(userService.verifyToken(token), HttpStatus.OK)
    }

    suspend fun orders(token: String): Flow<OrderDTO> {
        //Create a WebClient instance
        //building a client by using the DefaultWebClientBuilder class, which allows full customization
        val client: WebClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultCookie("cookie", token)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
            .build()

        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/orders")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue("")

        //Preparing a Request: define the Headers
        val responseSpec: WebClient.ResponseSpec = headersSpec.header(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
        )
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
            .acceptCharset(StandardCharsets.UTF_8)
            .ifNoneMatch("*")
            .ifModifiedSince(ZonedDateTime.now())
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        return headersSpec.exchangeToFlow { response: ClientResponse ->
            if (response.statusCode() == HttpStatus.OK) {
                return@exchangeToFlow response.bodyToFlow<OrderDTO>()
            //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToFlow response.bodyToFlow()
            } else {
                return@exchangeToFlow response.bodyToFlow()
            }
        }
    }
}
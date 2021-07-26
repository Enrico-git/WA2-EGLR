package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.services.UserDetailsService
import org.springframework.stereotype.Component
import com.expediagroup.graphql.spring.operations.Query
import it.polito.wa2.catalogservice.dto.*
import kotlinx.coroutines.flow.Flow
import org.springframework.http.*
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.reactive.function.client.exchangeToFlow
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.nio.charset.StandardCharsets
import java.util.*
import java.time.ZonedDateTime

@Component
//@Controller TODO try if graphql needs the Annotation @Controller
class CatalogQuery(private val userService: UserDetailsService): Query {

    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUser(registrationDTO: RegistrationDTO): UserDetailsDTO {
        return userService.registerUser(registrationDTO)
    }

    @ResponseStatus(HttpStatus.OK)
    suspend fun signIn(loginDTO: LoginDTO): LoginDTO {
        return userService.authAndCreateToken(loginDTO)
    }

    @ResponseStatus(HttpStatus.OK)
    suspend fun registrationConfirm(token: String): Unit {
        return userService.verifyToken(token)
    }

    //RETRIEVE ALL ORDERS OF A CUSTOMER
    @ResponseStatus(HttpStatus.OK)
    suspend fun orders(token: String): Flow<OrderDTO> {
        //Create a WebClient instance
        //building a client by using the DefaultWebClientBuilder class, which allows full customization
        val client: WebClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
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
                return@exchangeToFlow response.bodyToFlow<OrderDTO>()
            //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToFlow response.bodyToFlow()
            } else {
                return@exchangeToFlow response.bodyToFlow()
            }
        }
    }

    //RETRIEVE INFO ABOUT A WALLET GIVEN HIS WALLETID
    @ResponseStatus(HttpStatus.OK)
    suspend fun wallet(walletID: String, token: String): Mono<WalletDTO>? {
        //Create a WebClient instance
        //building a client by using the DefaultWebClientBuilder class, which allows full customization
        val client: WebClient = WebClient.builder()
            .baseUrl("http://localhost:8100")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8100"))
            .build()

        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets/$walletID")

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
                return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
            }
        }
    }

    //ADD A WALLET FOR A GIVEN CUSTOMER
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newWallet(customerID: String, token: String): Mono<WalletDTO> {
        //Create a WebClient instance
        //building a client by using the DefaultWebClientBuilder class, which allows full customization
        val client: WebClient = WebClient.builder()
            .baseUrl("http://localhost:8100")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8100"))
            .build()

        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        val wallet = CreateWalletDTO(customerID)
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(wallet)

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
                return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
            }
        }
    }

    //CREATE A TRANSACTION
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newTransaction(walletID: String, amount: BigDecimal, description: String,
                               orderID: String, token: String): Mono<TransactionDTO> {
        //Create a WebClient instance
        //building a client by using the DefaultWebClientBuilder class, which allows full customization
        val client: WebClient = WebClient.builder()
            .baseUrl("http://localhost:8100")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8100"))
            .build()

        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets/$walletID/transaction")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        val transaction = TransactionDTO(amount = amount, orderID = orderID, description = description,
            id = null, timestamp = null, walletID = null)
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(transaction)

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
                return@exchangeToMono response.bodyToMono(TransactionDTO::class.java)
                //TODO fix error cases
            } else if (response.statusCode().is4xxClientError) {
                return@exchangeToMono response.bodyToMono(TransactionDTO::class.java)
            } else {
                return@exchangeToMono response.bodyToMono(TransactionDTO::class.java)
            }
        }
    }
}
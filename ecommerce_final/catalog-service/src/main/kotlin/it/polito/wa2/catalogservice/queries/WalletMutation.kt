package it.polito.wa2.catalogservice.queries

import it.polito.wa2.catalogservice.dto.CreateWalletDTO
import it.polito.wa2.catalogservice.dto.TransactionDTO
import it.polito.wa2.catalogservice.dto.WalletDTO
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

@Component
class WalletMutation {

    val serviceURL = "http://localhost:8100"
    //Create a WebClient instance FOR WALLET SERVICE
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = WebClient.builder()
        .baseUrl(serviceURL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    //ADD A WALLET FOR A GIVEN CUSTOMER
    @ResponseStatus(HttpStatus.CREATED)
    fun newWallet(customerID: String, token: String): Mono<WalletDTO> {
        //Create a WebClient instance

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
            return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
        }
    }

    //CREATE A TRANSACTION
    @ResponseStatus(HttpStatus.CREATED)
    fun newTransaction(walletID: String, amount: BigDecimal, description: String?,
                       orderID: String, token: String): Mono<TransactionDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets/$walletID/transactions")

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
            return@exchangeToMono response.bodyToMono(TransactionDTO::class.java)
        }
    }
}
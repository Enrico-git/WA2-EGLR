package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.TransactionDTO
import it.polito.wa2.catalogservice.dto.WalletDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.util.*

@RestController
@RequestMapping("/wallets")
class WalletController {
    val serviceURL = "http://localhost:8100"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = WebClient.builder()
        .baseUrl(serviceURL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    //RETRIEVE INFO ABOUT A WALLET GIVEN ITS ID
    @GetMapping("/{walletID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getWallet(@PathVariable walletID: String): Mono<WalletDTO> {
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
            //.header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()

        //Get a response
        return headersSpec.exchangeToMono { response: ClientResponse ->
            return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
        }
    }

    //GET LIST OF TRANSACTION OF A GIVEN WALLET, OPTIONALLY IN A RANGE OF TIME
    @GetMapping("/{walletID}/transactions")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTransactions(@PathVariable walletID: Long,
                                @RequestParam from: Long?,
                                @RequestParam to: Long?): Flow<TransactionDTO> {
        //Preparing the request: define the URL
        var query = ""
        if(from!=null)
            query = "?from=$from"
        if(to!=null && query!="") {
            query += "&to=$to"
        } else if(to!=null && query=="") {
            query += "?to=$to"
        }
        return ReactiveSecurityContextHolder.getContext().flatMapMany {
            val token = it.authentication.credentials as String
            return@flatMapMany client.get().uri("$serviceURL/wallets/$walletID/transactions$query")
                .accept(MediaType.APPLICATION_NDJSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .bodyToFlux<TransactionDTO>()
        }.asFlow()
    }

    //RETRIEVE THE INFO OF A TRANSACTION GIVEN ITS ID
    @GetMapping("/{walletID}/transactions/{transactionID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTransaction(@PathVariable walletID: String,
                               @PathVariable transactionID: String): Mono<TransactionDTO>? {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets/$walletID/transactions/$transactionID")

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
            return@exchangeToMono response.bodyToMono(TransactionDTO::class.java)
        }
    }

    //ADD A WALLET FOR A GIVEN CUSTOMER
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newWallet(@RequestBody walletDTO: WalletDTO): Mono<WalletDTO> {

        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets")

        //Preparing a Request: define the Body
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(walletDTO)

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
            return@exchangeToMono response.bodyToMono(WalletDTO::class.java)
        }
    }

    //CREATE A TRANSACTION
    @PostMapping("/{walletID}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun newTransaction(@PathVariable walletID: String,
                               @RequestBody transactionDTO: TransactionDTO): Mono<TransactionDTO> {
        //specify an HTTP method of a request by invoking method(HttpMethod method)
        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.POST)

        //Preparing the request: define the URL
        var bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/wallets/$walletID/transactions")

        //Preparing a Request: define the Body
        //in this case there is no body in the Request
        var headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec.bodyValue(transactionDTO)

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
            return@exchangeToMono response.bodyToMono(TransactionDTO::class.java)
        }
    }
}
package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.TransactionDTO
import it.polito.wa2.catalogservice.dto.WalletDTO
import it.polito.wa2.catalogservice.exceptions.NotFoundException
import it.polito.wa2.catalogservice.exceptions.UnauthorizedException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToFlow
import java.util.*
import java.util.function.Predicate

@Service
class WalletServiceImpl(
    @Qualifier("wallet-service-client") private val loadBalancedWebClientBuilder: WebClient.Builder
): WalletService {
    val serviceURL = "http://wallet-service"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = loadBalancedWebClientBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    override suspend fun getWallet(walletID: String): WalletDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .get()
            .uri("$serviceURL/wallets/$walletID")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.NOT_FOUND }) { throw NotFoundException("Order not found") }
            .onStatus(Predicate { it == HttpStatus.FORBIDDEN }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun getTransactions(walletID: Long, from: Long?, to: Long?): Flow<TransactionDTO> {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        var query = ""
        if(from!=null)
            query = "?from=$from"
        if(to!=null && query!="") {
            query += "&to=$to"
        } else if(to!=null && query=="") {
            query += "?to=$to"
        }
        return client
            .get()
            .uri("$serviceURL/wallets/$walletID/transactions$query")
            .accept(MediaType.APPLICATION_NDJSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .bodyToFlow()
    }

    override suspend fun getTransaction(walletID: String, transactionID: String): TransactionDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .get()
            .uri("$serviceURL/wallets/$walletID/transactions/$transactionID")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.NOT_FOUND }) { throw NotFoundException("Order not found") }
            .onStatus(Predicate { it == HttpStatus.FORBIDDEN }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun newWallet(walletDTO: WalletDTO): WalletDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .post()
            .uri("$serviceURL/wallets/")
            .bodyValue(walletDTO)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun newTransaction(walletID: String, transactionDTO: TransactionDTO): TransactionDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .post()
            .uri("$serviceURL/wallets/$walletID/transactions")
            .bodyValue(transactionDTO)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw RuntimeException("Something went wrong") }
            .awaitBody()
    }
}
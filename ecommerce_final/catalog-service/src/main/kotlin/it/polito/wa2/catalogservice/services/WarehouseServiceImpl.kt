package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.WarehouseDTO
import it.polito.wa2.catalogservice.exceptions.NotFoundException
import it.polito.wa2.catalogservice.exceptions.UnauthorizedException
import it.polito.wa2.catalogservice.exceptions.UnavailableServiceException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToFlow
import java.util.*
import java.util.function.Predicate

@Service
class WarehouseServiceImpl(
    @Qualifier("warehouse-service-client") private val loadBalancedWebClientBuilder: WebClient.Builder
): WarehouseService {
    val serviceURL = "http://warehouse-service"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = loadBalancedWebClientBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    override suspend fun getWarehouses(): Flow<WarehouseDTO> {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .get()
            .uri("$serviceURL/warehouses")
            .accept(MediaType.APPLICATION_NDJSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .bodyToFlow()
    }

    override suspend fun getWarehouse(warehouseID: String): WarehouseDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .get()
            .uri("$serviceURL/warehouses/$warehouseID")
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.NOT_FOUND }) { throw NotFoundException("Warehouse not found") }
            .onStatus(Predicate { it == HttpStatus.FORBIDDEN }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun deleteWarehouse(warehouseID: String) {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        client
            .method(HttpMethod.DELETE)
            .uri("$serviceURL/warehouses/$warehouseID")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.FORBIDDEN }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("The warehouse does not exist") }
            .awaitBodilessEntity()
    }

    override suspend fun newWarehouse(warehouseDTO: WarehouseDTO): WarehouseDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .post()
            .uri("$serviceURL/warehouses")
            .bodyValue(warehouseDTO)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun patchWarehouse(warehouseID: String, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .patch()
            .uri("$serviceURL/warehouses/$warehouseID")
            .bodyValue(warehouseDTO)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("The warehouse does not exist") }
            .awaitBody()
    }

    override suspend fun updateWarehouse(warehouseID: String, warehouseDTO: WarehouseDTO): WarehouseDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .put()
            .uri("$serviceURL/warehouses/$warehouseID")
            .bodyValue(warehouseDTO)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Nice try") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("The warehouse does not exist") }
            .awaitBody()
    }
}
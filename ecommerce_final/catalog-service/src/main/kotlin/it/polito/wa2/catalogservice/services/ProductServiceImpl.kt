package it.polito.wa2.catalogservice.services

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.dto.PictureDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import it.polito.wa2.catalogservice.exceptions.NotFoundException
import it.polito.wa2.catalogservice.exceptions.UnauthorizedException
import it.polito.wa2.catalogservice.exceptions.UnavailableServiceException
import it.polito.wa2.catalogservice.exceptions.WebClientBadRequestException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitSingle
import org.bson.types.ObjectId
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
class ProductServiceImpl(
    @Qualifier("warehouse-service-client") private val loadBalancedWebClientBuilder: WebClient.Builder
): ProductService {
    val serviceURL = "http://warehouse-service"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = loadBalancedWebClientBuilder
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    override suspend fun getProducts(category: String?, page: Int?, size: Int?): Flow<ProductDTO> {
        val categoryOpt = if ( category != null) Optional.of(category) else Optional.empty()
        val pageOpt = if ( page != null) Optional.of(page) else Optional.empty()
        val sizeOpt = if ( size != null) Optional.of(size) else Optional.empty()
        return client
            .get()
            .uri{
                it.host("warehouse-service").path("/products")
                    .queryParamIfPresent("category", categoryOpt)
                    .queryParamIfPresent("page", pageOpt)
                    .queryParamIfPresent("size", sizeOpt)
                    .build()
            }
            .accept(MediaType.APPLICATION_NDJSON)
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .bodyToFlow()
    }

    override suspend fun getProductsByIDS(ids: List<String>): Flow<ProductDTO> {
        return client
            .get()
            .uri{
                it.host("warehouse-service").path("/products")
                    .queryParam("ids", ids)
                    .build()
            }
            .accept(MediaType.APPLICATION_NDJSON)
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .bodyToFlow()
    }

    override suspend fun getProduct(productID: ObjectId): ProductDTO {
        return client
            .get()
            .uri("$serviceURL/products/$productID")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.NOT_FOUND }) { throw NotFoundException("Product not found") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun getProductPicture(productID: ObjectId): PictureDTO {
        return client
            .get()
            .uri("$serviceURL/products/$productID/picture")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("Product not found") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun getProductWarehouses(productID: ObjectId): Flow<WarehouseDTO> {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .get()
            .uri("$serviceURL/products/$productID/warehouses")
            .accept(MediaType.APPLICATION_NDJSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .bodyToFlow()
    }

    override suspend fun addProduct(productDTO: ProductDTO): ProductDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .post()
            .uri("$serviceURL/products")
            .bodyValue(productDTO)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { it.bodyToMono(WebClientBadRequestException::class.java)}
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Unauthorized") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .awaitBody()
    }

    override suspend fun deleteProduct(productID: ObjectId) {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        client
            .method(HttpMethod.DELETE)
            .uri("$serviceURL/products/$productID")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Unauthorized") }
            .onStatus(Predicate { it == HttpStatus.FORBIDDEN }) { throw UnauthorizedException("Unauthorized") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("The product does not exist") }
            .awaitBodilessEntity()
    }

    override suspend fun updatePicture(productID: ObjectId, pictureDTO: PictureDTO): ProductDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .post()
            .uri("$serviceURL/products/$productID/picture")
            .bodyValue(pictureDTO)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Unauthorized") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("The product does not exist") }
            .awaitBody()
    }

    override suspend fun patchProduct(productID: ObjectId, productDTO: ProductDTO): ProductDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .patch()
            .uri("$serviceURL/products/$productID")
            .bodyValue(productDTO)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Unauthorized") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("The product does not exist") }
            .awaitBody()
    }

    override suspend fun updateProduct(productID: ObjectId, productDTO: ProductDTO): ProductDTO {
        val token = ReactiveSecurityContextHolder.getContext().awaitSingle().authentication.credentials as String
        return client
            .put()
            .uri("$serviceURL/products/$productID")
            .bodyValue(productDTO)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .retrieve()
            .onStatus(Predicate { it == HttpStatus.UNAUTHORIZED }) { throw UnauthorizedException("Unauthorized") }
            .onStatus(Predicate { it == HttpStatus.INTERNAL_SERVER_ERROR }) { throw UnavailableServiceException("Something went wrong") }
            .onStatus(Predicate { it == HttpStatus.BAD_REQUEST }) { throw IllegalArgumentException("The product does not exist") }
            .awaitBody()
    }
}
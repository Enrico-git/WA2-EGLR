package it.polito.ecommerce.warehouse.unit

import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import it.polito.ecommerce.warehouse.controllers.ProductController
import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.services.ProductService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.math.BigDecimal

@WebFluxTest(ProductController::class)
class ProductControllerUnitTests(
    @Autowired private val controller: ProductController
) {

    @MockkBean
    private lateinit var productService: ProductService

    @PostMapping("/products")
    suspend fun createProduct(@RequestBody productDTO: ProductDTO): ResponseEntity<Any> {
        return ResponseEntity(productService.addProduct(productDTO), HttpStatus.CREATED)
    }

    @Test
    fun `assert create Product returns status 201 when successfully called`() {
        val productDTO = ProductDTO(
            id = null,
            name = "Eggplants",
            category = "Food",
            price = BigDecimal("2"),
            quantity = 2000
        )

        coEvery { productService.addProduct(productDTO) } returns productDTO
        val out = ResponseEntity(productDTO, HttpStatus.CREATED)

        runBlocking { assert(controller.createProduct(productDTO) == out) }

    }
}
package it.polito.ecommerce.warehouse.controllers

import it.polito.ecommerce.warehouse.domain.Product
import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.repositories.ProductRepository
import it.polito.ecommerce.warehouse.services.ProductService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.Min

@RestController
@RequestMapping("/warehouse")
@Validated
class ProductController(
    private val productRepository: ProductRepository,
    private val productService : ProductService
) {
    @PostMapping("/products")
    fun createProduct(@RequestBody productDTO: Product): ResponseEntity<Any> {
        return ResponseEntity( HttpStatus.CREATED)
    }

    @PatchMapping("/products/{productID}")
    fun updateProduct(@RequestBody productDTO: Product): ResponseEntity<Any> {
        return ResponseEntity( HttpStatus.CREATED)
    }

    @GetMapping("/products/{productID}")
    suspend fun getProductByID(@PathVariable @Min(0) productID: Long): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.getProductById(productID), HttpStatus.OK)
    }

    @GetMapping("/products")
    fun getAllProducts(): ResponseEntity<Any> {
        return ResponseEntity( HttpStatus.OK)
    }

    @GetMapping("/productsByCategory")
    fun getProductsByCategory(@RequestParam category: String): ResponseEntity<Flow<Product>> {
        return ResponseEntity( productRepository.findAllByCategory(category), HttpStatus.OK )
    }
}
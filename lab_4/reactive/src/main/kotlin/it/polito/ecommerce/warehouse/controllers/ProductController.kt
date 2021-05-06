package it.polito.ecommerce.warehouse.controllers

import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.repositories.ProductRepository
import it.polito.ecommerce.warehouse.services.ProductService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/warehouse")
//@Validated
class ProductController(
    private val productRepository: ProductRepository,
    private val productService : ProductService
) {
    @PostMapping("/products")
    suspend fun createProduct(@RequestBody productDTO: ProductDTO): ResponseEntity<Any> {
        return ResponseEntity( productService.addProduct(productDTO), HttpStatus.CREATED)
    }

    @PatchMapping("/products/{productID}")
    suspend fun updateProduct(@PathVariable productID: Long, @RequestBody productDTO: ProductDTO): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.updateProduct(productID, productDTO) ,HttpStatus.CREATED)
    }

    @GetMapping("/products/{productID}")
    suspend fun getProductByID(@PathVariable productID: Long): ResponseEntity<ProductDTO> {
        println(productID)
        return ResponseEntity(productService.getProductById(productID), HttpStatus.OK)
    }

    @GetMapping("/products", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getAllProducts(): ResponseEntity<Flow<ProductDTO>> {
        return ResponseEntity( productService.getAllProducts(),HttpStatus.OK)
    }

    @GetMapping("/productsByCategory", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getProductsByCategory(@RequestParam category: String): ResponseEntity<Flow<ProductDTO>> {
        return ResponseEntity( productService.getProductsByCategory(category), HttpStatus.OK )
    }
}
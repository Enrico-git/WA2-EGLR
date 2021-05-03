package it.polito.ecommerce.warehouse.controllers

import it.polito.ecommerce.warehouse.domain.Product
import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.services.ProductService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/warehouse")
@Validated
class ProductController(
    private val productService: ProductService
) {
    @PostMapping("/products")
    suspend fun createProduct(@RequestBody productDTO: ProductDTO): ResponseEntity<Any> {
        return ResponseEntity(productService.addProduct(productDTO), HttpStatus.CREATED)
    }

    @PatchMapping("/products/{productID}")
    suspend fun updateProduct(@PathVariable productID: Long,
                              @RequestBody productDTO: ProductDTO): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.updateProduct(productID, productDTO), HttpStatus.OK)
    }

    @GetMapping("/products/{productID}")
    suspend fun getProduct(@PathVariable productID: Long): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.getProductById(productID), HttpStatus.OK)
    }

    @GetMapping("/products")
    suspend fun getAllProducts(): ResponseEntity<Flow<ProductDTO>> {
        return ResponseEntity(productService.getAllProducts(), HttpStatus.OK)
    }

    @GetMapping("/productsByCategory")
    suspend fun getProductsByCategory(@RequestParam category: String): ResponseEntity<Any> {
        return ResponseEntity(productService.getProductsByCategory(category), HttpStatus.OK)
    }
}
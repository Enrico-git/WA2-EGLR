package it.polito.ecommerce.warehouse.controllers

import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.services.ProductService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/warehouse")
@Validated
class ProductController(
    private val productService: ProductService
) {
    @PostMapping("/products")
    fun createProduct(@RequestBody productDTO: ProductDTO): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.addProduct(productDTO), HttpStatus.CREATED)
    }

    @PatchMapping("/products/{productID}")
    fun updateProduct(@PathVariable @Valid @Min(0) productID: Long,
                      @RequestBody productDTO: ProductDTO): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.updateProduct(productID, productDTO), HttpStatus.OK)
    }

    @GetMapping("/products/{productID}")
    fun getProduct(@PathVariable @Valid @Min(0) productID: Long): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.getProductById(productID), HttpStatus.OK)
    }

    @GetMapping("/products")
    fun getAllProducts(pageable: Pageable): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity(productService.getAllProducts(pageable), HttpStatus.OK)
    }

    @GetMapping("/productsByCategory")
    fun getProductsByCategory(@RequestParam category: String, pageable: Pageable): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity(productService.getProductsByCategory(category, pageable), HttpStatus.OK)
    }
}
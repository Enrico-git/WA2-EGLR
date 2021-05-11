package it.polito.ecommerce.warehouse.controllers

import it.polito.ecommerce.warehouse.dto.ProductDTO
import it.polito.ecommerce.warehouse.services.ProductService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/warehouse")
@Validated
class ProductController(
    private val productService: ProductService
) {
    @PostMapping("/products")
    fun createProduct(@RequestBody @Valid productDTO: ProductDTO): ResponseEntity<Any> {
        return ResponseEntity(productService.addProduct(productDTO), HttpStatus.CREATED)
    }

    @PatchMapping("/products/{productID}")
    fun updateProduct(
        @PathVariable productID: Long,
        @RequestBody @Valid productDTO: ProductDTO
    ): ResponseEntity<ProductDTO> {
        var counter = 5
        while (counter-- > 0) {
            try {
                return ResponseEntity(productService.updateProduct(productID, productDTO), HttpStatus.CREATED)
            } catch (e: ObjectOptimisticLockingFailureException) {
                Thread.sleep(1000)
            }
        }
        throw ObjectOptimisticLockingFailureException("Product", 1)
    }

    @GetMapping("/products/{productID}")
    fun getProductByID(@PathVariable productID: Long): ResponseEntity<ProductDTO> {
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
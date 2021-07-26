package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.services.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
@Validated
class ProductController(
        private val productService: ProductService
) {
    @GetMapping("/{productID}")
    suspend fun getProduct(@PathVariable productID: String): ResponseEntity<ProductDTO>{
        val product = productService.getProductById(productID)
        println(product)
        return ResponseEntity(product, HttpStatus.OK)
    }
}
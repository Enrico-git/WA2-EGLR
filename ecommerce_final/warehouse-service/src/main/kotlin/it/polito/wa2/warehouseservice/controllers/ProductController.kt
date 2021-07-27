package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.services.ProductService
import org.bson.types.ObjectId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
        private val productService: ProductService
) {
    /**
     * API endpoint to retrieve a product by its ID
     * @param productID the ID of the product
     * @return the product object
     */
    @GetMapping("/{productID}")
    suspend fun getProduct(@PathVariable productID: String): ProductDTO{
        println("controller")
        val product = productService.getProductById(ObjectId(productID))
        println(product)
        return product
    }
}
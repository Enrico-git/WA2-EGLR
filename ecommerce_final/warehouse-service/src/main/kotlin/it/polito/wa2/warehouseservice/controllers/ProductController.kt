package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.services.ProductService
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
        private val productService: ProductService
) {
    /**
     * API endpoint to retrieve a list of products by their category
     * @param category which is the category to search
     * @returnt the flow of the products
     */
    @GetMapping()
    suspend fun getProductByCategory(@RequestParam category: String, pageable: Pageable): Flow<ProductDTO> {
        return productService.getProductsByCategory(category, pageable)
    }
    /**
     * API endpoint to retrieve a product by its ID
     * @param productID the ID of the product
     * @return the product object
     */
    @GetMapping("/{productID}")
    suspend fun getProduct(@PathVariable productID: String): ProductDTO{
        return productService.getProductById(ObjectId(productID))
    }


}
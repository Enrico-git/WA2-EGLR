package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.services.ProductService
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
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
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
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
    /**
     * API endpoint to insert a product
     * @param productDTO
     * @return the product object
     */
    @PostMapping("")
    suspend fun addProduct(@RequestBody productDTO: ProductDTO): ProductDTO{
        return productService.addProduct(productDTO)
    }
    /**
     * API endpoint to modify or insert a product
     * @param productID the ID of the product, @param productDTO which is the product to insert or it owns the product's information to change
     * @return the product object
     * Being a PUT we need the entire ProductDTO
     */
    @PutMapping("/{productID}")
    suspend fun modifyOrInsertProduct(@PathVariable productID: String, productDTO: ProductDTO): ProductDTO{
        return productService.modifyProduct(productDTO, ObjectId(productID))
    }
    /**
     * API endpoint to partial modify a product
     * @param productID the ID of the product
     * @param productDTO the DTO with the elements to modify
     * @return ProductDTO
     */
    @PatchMapping("/{productID}")
    suspend fun partialUpdateProduct(@PathVariable productID: String, @RequestBody productDTO: ProductDTO): ProductDTO{
        return productService.partialUpdateProduct(productDTO, ObjectId(productID))
    }
    /**
     * API endpoint to delete a product
     * @param productID the ID of the product
     * @return nothing
     */
    @DeleteMapping("/{productID}")
    suspend fun deleteProduct(@PathVariable productID: String){
        return productService.deleteProduct(ObjectId(productID))
    }

    /**
     * API endpoint to get the product's picture
     * @param productID the ID of the product
     * @return the string of the picture
     */
    @GetMapping("/{productID}/picture")
    suspend fun getProductPicture(@PathVariable productID: String): String{
        return productService.getProductPicture(ObjectId(productID))
    }

    /**
     * API endpoint to modify the product's picture
     * @param productID the ID of the product, the body is the picture (a string)
     * @return the product object
     */
    @PostMapping("/{productID}/picture")
    suspend fun modifyProductPicture(@PathVariable productID: String, @RequestBody pictureURL: String): ProductDTO{
        return productService.modifyProductPicture(pictureURL, ObjectId(productID))
    }

    /**
     * API endpoint to get all the comments of the product
     * @param productID the ID of the product
     * @return flow of CommentDTO
     */
    @GetMapping("/{productID}/comments")
    suspend fun getProductComments(@PathVariable productID: String): Flow<CommentDTO>{
        return productService.getProductComments(ObjectId(productID))
    }

    /**
     * API endpoint to get all the warehouses which contain the productID
     * @param productID the ID of the product
     * @return flow of WarehouseDTO
     */
    @GetMapping("/{productID}/warehouses")
    suspend fun getProductWarehouses(@PathVariable productID: String): Flow<WarehouseDTO>{
        return productService.getProductWarehouses(ObjectId(productID))
    }

}
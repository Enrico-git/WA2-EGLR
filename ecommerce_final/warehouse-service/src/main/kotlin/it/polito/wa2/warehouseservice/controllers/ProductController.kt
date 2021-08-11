package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.constraintGroups.CreateOrReplaceProduct
import it.polito.wa2.warehouseservice.constraintGroups.CreateProduct
import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.dto.PictureDTO
import it.polito.wa2.warehouseservice.dto.ProductDTO
import it.polito.wa2.warehouseservice.dto.WarehouseDTO
import it.polito.wa2.warehouseservice.services.ProductService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
        private val productService: ProductService
) {
    /**
     * API endpoint to retrieve a list of products by their category
     * @param category which is the category to search
     * @param pageable the pagination details
     * @return the flow of the products
     */
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    suspend fun getProductByCategory(@RequestParam category: String?, pageable: Pageable): Flow<ProductDTO> {
        return productService.getProductsByCategory(category, pageable)
    }
    /**
     * API endpoint to retrieve a product by its ID
     * @param productID the ID of the product
     * @return the product object
     */
    @GetMapping("/{productID}")
    suspend fun getProduct(@PathVariable productID: ObjectId): ProductDTO{
        return productService.getProductById(productID)
    }

    /**
     * API endpoint to insert a product
     * @param productDTO
     * @return the product object
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addProduct(@RequestBody @Validated(CreateProduct::class) productDTO: ProductDTO): ProductDTO{
        return productService.addProduct(productDTO)
    }
    /**
     * API endpoint to modify or insert a product
     * @param productID the ID of the product, @param productDTO which is the product to insert or it owns the product's information to change
     * @return the product object
     * Being a PUT we need the entire ProductDTO
     */
    @PutMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun modifyOrInsertProduct(@PathVariable productID: String, @RequestBody @Validated(CreateOrReplaceProduct::class) productDTO: ProductDTO): ProductDTO{
        var counter = 5
        while(counter-- > 0){
            try{
                return productService.modifyProduct(productDTO, ObjectId(productID))
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Product")
    }
    /**
     * API endpoint to partial modify a product
     * @param productID the ID of the product
     * @param productDTO the DTO with the elements to modify
     * @return ProductDTO
     */
    @PatchMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun partialUpdateProduct(@PathVariable productID: String, @RequestBody productDTO: ProductDTO): ProductDTO{
        var counter = 5
        while (counter-- > 0)
            try {
                return productService.partialUpdateProduct(productDTO, ObjectId(productID))
            } catch (e : OptimisticLockingFailureException){
                delay(1000)
            }
        throw OptimisticLockingFailureException("Product")
    }
    /**
     * API endpoint to delete a product
     * @param productID the ID of the product
     * @return nothing
     */
    @DeleteMapping("/{productID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteProduct(@PathVariable productID: ObjectId){
        var counter = 5
        while(counter-- > 0){
            try{
                return productService.deleteProduct(productID)
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Product")
    }

    /**
     * API endpoint to get the product's picture
     * @param productID the ID of the product
     * @return the string of the picture
     */
    @GetMapping("/{productID}/picture")
    suspend fun getProductPicture(@PathVariable productID: ObjectId): PictureDTO{
        return productService.getProductPicture(productID)
    }

    /**
     * API endpoint to modify the product's picture
     * @param productID the ID of the product, the body is the picture (a string)
     * @return the product object
     */
    @PostMapping("/{productID}/picture")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun modifyProductPicture(@PathVariable productID: String, @RequestBody pictureDTO: PictureDTO): ProductDTO{
        var counter = 5
        while(counter-- > 0){
            try{
                return productService.modifyProductPicture(pictureDTO, ObjectId(productID))
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Product")
    }

    /**
     * API endpoint to get all the comments of the product
     * @param productID the ID of the product
     * @return flow of CommentDTO
     */
    @GetMapping("/{productID}/comments")
    suspend fun getProductComments(@PathVariable productID: ObjectId): Flow<CommentDTO>{
        return productService.getProductComments(productID)
    }

    /**
     * API endpoint to get all the warehouses which contain the productID
     * @param productID the ID of the product
     * @return flow of WarehouseDTO
     */
    @GetMapping("/{productID}/warehouses")
    suspend fun getProductWarehouses(@PathVariable productID: ObjectId): Flow<WarehouseDTO>{
        return productService.getProductWarehouses(productID)
    }

}
package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.constraintGroups.CreateOrReplaceProduct
import it.polito.wa2.catalogservice.constraintGroups.CreateProduct
import it.polito.wa2.catalogservice.dto.PictureDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import it.polito.wa2.catalogservice.services.ProductService
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
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
     * API endpoint to retrieve a list of products by their category (if present)
     * no need of authentication
     * @param category which is the category to search
     * @param page the page number of the pagination
     * @param size the size of the pagination
     * @return the flow of the products
     */
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProducts(@RequestParam category: String?,
                            @RequestParam page: Int?,
                            @RequestParam size: Int?): Flow<ProductDTO> {
        return productService.getProducts(category,page,size)
    }

    /**
     * API endpoint to retrieve a product by its ID
     * no need of authentication
     * @param productID the ID of the product
     * @return the product object
     */
    @GetMapping("/{productID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProduct(@PathVariable productID: ObjectId): ProductDTO {
        return productService.getProduct(productID)
    }

    /**
     * API endpoint to get the product's picture
     * no need of authentication
     * @param productID the ID of the product
     * @return the string of the picture
     */
    @GetMapping("/{productID}/picture")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProductPicture(@PathVariable productID: ObjectId): PictureDTO {
       return productService.getProductPicture(productID)
    }

    /**
     * API endpoint to get all the warehouses which contain the productID
     * @param productID the ID of the product
     * @return flow of WarehouseDTO
     */
    @GetMapping("/{productID}/warehouses")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProductWarehouses(@PathVariable productID: ObjectId): Flow<WarehouseDTO> {
       return productService.getProductWarehouses(productID)
    }

    /**
     * API endpoint to delete a product
     * @param productID the ID of the product
     * @return nothing
     */
    @DeleteMapping("/{productID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteProduct(@PathVariable productID: ObjectId) {
        return productService.deleteProduct(productID)
    }

    /**
     * API endpoint to modify the product's picture
     * @param productID the ID of the product, the body is the picture (a string)
     * @return the product object
     */
    @PostMapping("/{productID}/picture")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updatePicture(@PathVariable productID: ObjectId,
                              @RequestBody pictureDTO: PictureDTO): ProductDTO {
        return productService.updatePicture(productID,pictureDTO)
    }

    /**
     * API endpoint to insert a product
     * @param productDTO
     * @return the product object
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addProduct(@RequestBody @Validated(CreateProduct::class) productDTO: ProductDTO): ProductDTO {
        return productService.addProduct(productDTO)
    }

    /**
     * API endpoint to partial modify a product
     * @param productID the ID of the product
     * @param productDTO the DTO with the elements to modify
     * @return ProductDTO
     */
    @PatchMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun patchProduct(@PathVariable productID: ObjectId,
                             @RequestBody productDTO: ProductDTO): ProductDTO {
        return productService.patchProduct(productID,productDTO)
    }

    /**
     * API endpoint to modify or insert a product
     * @param productID the ID of the product, @param productDTO which is the product to insert or it owns the product's information to change
     * @return the product object
     * Being a PUT we need the entire ProductDTO
     */
    @PutMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateProduct(@PathVariable productID: ObjectId,
                              @RequestBody @Validated(CreateOrReplaceProduct::class) productDTO: ProductDTO): ProductDTO {
        return productService.updateProduct(productID,productDTO)
    }
}

package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.dto.PictureDTO
import it.polito.wa2.catalogservice.dto.ProductDTO
import it.polito.wa2.catalogservice.dto.WarehouseDTO
import it.polito.wa2.catalogservice.services.ProductService
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {
    //RETRIEVE ALL THE PRODUCTS, OR ALL THE PRODUCTS OF A GIVEN CATEGORY
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProducts(@RequestParam category: String?,
                            @RequestParam page: Int?,
                            @RequestParam size: Int?): Flow<ProductDTO> {
        return productService.getProducts(category,page,size)
    }

    //RETRIEVE INFO ABOUT A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProduct(@PathVariable productID: String): ProductDTO {
        return productService.getProduct(productID)
    }

    //RETRIEVE THE PICTURE URL OF A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}/picture")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProductPicture(@PathVariable productID: String): PictureDTO {
       return productService.getProductPicture(productID)
    }

    //RETRIEVE THE LIST OF WAREHOUSES THAT CONTAIN A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}/warehouses")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProductWarehouses(@PathVariable productID: String): Flow<WarehouseDTO> {
        return productService.getProductWarehouses(productID)
    }

    //RETRIEVE THE LIST OF WAREHOUSES THAT CONTAIN A PRODUCT GIVEN ITS ID
    //NO NEED OF AUTHENTICATION -> NO TOKEN
    @GetMapping("/{productID}/comments")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProductComments(@PathVariable productID: String): Flow<CommentDTO> {
        return productService.getProductComments(productID)
    }

    //DELETE A PRODUCT GIVEN ITS ID
    @DeleteMapping("/{productID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun deleteProduct(@PathVariable productID: String) {
        return productService.deleteProduct(productID)
    }

    //UPDATE THE PICTURE OF A PRODUCT GIVEN ITS ID AND THE NEW PICTURE
    @PostMapping("/{productID}/picture")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updatePicture(@PathVariable productID: String,
                              @RequestBody pictureDTO: PictureDTO): ProductDTO {
        return productService.updatePicture(productID,pictureDTO)
    }

    //PARTIALLY UPDATE A PRODUCT GIVEN ITS ID
    @PatchMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun patchProduct(@PathVariable productID: String,
                             @RequestBody productDTO: ProductDTO): ProductDTO {
        return productService.patchProduct(productID,productDTO)
    }

    //UPDATE A PRODUCT GIVEN ITS ID, OR ADD A NEW ONE IF THE ID DOES NOT EXIST
    @PutMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateProduct(@PathVariable productID: String,
                              @RequestBody productDTO: ProductDTO): ProductDTO {
        return productService.updateProduct(productID,productDTO)
    }
}

package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.services.CommentService
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products/{productID}/comments")
class CommentController(
    private val commentService: CommentService
) {
    /**
     * API endpoint to get the list of comments of a product
     * @param productID the ID of the product
     * @return flow of CommentDTO
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProductComments(@PathVariable productID: ObjectId): Flow<CommentDTO> {
        return commentService.getComments(productID)
    }

    /**
     * API endpoint to get a comment
     * @param productID the ID of the product
     * @param commentID the ID of the comment
     * @return CommentDTO object
     */
    @GetMapping("/{commentID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getComment(@PathVariable productID: ObjectId, @PathVariable commentID: ObjectId): CommentDTO {
        return commentService.getComment(productID, commentID)
    }

    /**
     * API endpoint to insert a new comment
     * @param productID the ID of the product, the body is the comment
     * @return the comment object
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addComment(@PathVariable productID: ObjectId,
                           @RequestBody @Validated commentDTO: CommentDTO): CommentDTO {
        return commentService.addComment(productID,commentDTO)
    }

    /**
     * API endpoint to update a comment
     * @param productID the ID of the product
     * @requestBody CommentDTO
     * @return the comment object
     */
    @PutMapping("/{commentID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateComment(@PathVariable productID: ObjectId,
                              @PathVariable commentID: ObjectId,
                              @RequestBody @Validated commentDTO: CommentDTO): CommentDTO {
        return commentService.updateComment(productID,commentID,commentDTO)
    }

    /**
     * API endpoint to delete a comment
     * @param productID the ID of the product
     * @param commentID the id of the comment
     * @return
     */
    @DeleteMapping("/{commentID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteComment(@PathVariable productID: ObjectId, @PathVariable commentID: ObjectId) {
        return commentService.deleteComment(productID,commentID)
    }
}
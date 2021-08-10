package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.services.CommentService
import kotlinx.coroutines.delay
import org.bson.types.ObjectId
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products/{productID}/comments")
class CommentController(
        private val commentService: CommentService
) {
    /**
     * API endpoint to insert a new comment
     * @param productID the ID of the product, the body is the comment
     * @return the comment object
     */
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addComment(@PathVariable productID: String, @RequestBody @Validated commentDTO: CommentDTO): CommentDTO {
        return commentService.addComment(ObjectId(productID), commentDTO)
    }

    /**
     * API endpoint to update a comment
     * @param productID the ID of the product
     * @requestBody CommentDTO
     * @return the comment object
     */
    @PutMapping("/{commentID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateComment(@PathVariable productID: String, @PathVariable commentID: String, @Validated @RequestBody commentDTO: CommentDTO): CommentDTO {
        var counter = 5
        while(counter-- > 0){
            try{
                return commentService.updateComment(ObjectId(productID), ObjectId(commentID), commentDTO)
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Comment")
    }

    /**
     * API endpoint to delete a comment
     * @param productID the ID of the product
     * @param commentID the id of the comment
     * @return
     */
    @DeleteMapping("/{commentID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteComment(@PathVariable productID: String, @PathVariable commentID: String) {
        var counter = 5
        while(counter-- > 0){
            try{
                return commentService.deleteComment(ObjectId(productID), ObjectId(commentID))
            }
            catch(e: OptimisticLockingFailureException){
                delay(1000)
            }
        }
        throw OptimisticLockingFailureException("Comment")
    }

    /**
     * API endpoint to get a comment
     * @param commentID the ID of the product
     * @return the comment object
     */
    @GetMapping("/{commentID}")
    suspend fun getComment(@PathVariable commentID: String): CommentDTO{
        return commentService.getComment(ObjectId(commentID))
    }
    

}
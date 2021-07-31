package it.polito.wa2.warehouseservice.controllers

import it.polito.wa2.warehouseservice.dto.CommentDTO
import it.polito.wa2.warehouseservice.services.CommentService
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/comments")
class CommentController(
        private val commentService: CommentService
) {
    /**
     * API endpoint to insert a new comment
     * @param productID the ID of the product, the body is the comment
     * @return the comment object
     */
    @PostMapping("/{productID}")
    suspend fun addComment(@PathVariable productID: String, @RequestBody commentDTO: CommentDTO): CommentDTO {
        return commentService.addComment(ObjectId(productID), commentDTO)
    }

    /**
     * API endpoint to update a comment
     * @param productID the ID of the product, the body is the comment
     * @return the comment object
     */
    @PutMapping("/{productID}/{commentID}")
    suspend fun updateComment(@PathVariable productID: String, @PathVariable commentID: String, @RequestBody commentDTO: CommentDTO): CommentDTO {
        return commentService.updateComment(ObjectId(productID), ObjectId(commentID), commentDTO)
    }

    /**
     * API endpoint to update a comment
     * @param productID the ID of the product, the body is the comment
     * @return the comment object
     */
    @DeleteMapping("/{productID}/{commentID}")
    suspend fun deleteComment(@PathVariable productID: String, @PathVariable commentID: String) {
        return commentService.deleteComment(ObjectId(productID), ObjectId(commentID))
    }

    /**
     * API endpoint to get a comment
     * @param productID the ID of the product
     * @return the comment object
     */
    @DeleteMapping("/{productID}/{commentID}")
    suspend fun getComment(@PathVariable commentID: String): CommentDTO {
        return commentService.getComment(ObjectId(commentID))
    }
    
    

}
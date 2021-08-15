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
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getProductComments(@PathVariable productID: ObjectId): Flow<CommentDTO> {
        return commentService.getComments(productID)
    }
    //RETRIEVE INFO ABOUT A COMMENT GIVEN ITS ID
    @GetMapping("/{commentID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getComment(@PathVariable productID: ObjectId, @PathVariable commentID: ObjectId): CommentDTO {
        return commentService.getComment(productID, commentID)
    }

    //CREATE A NEW COMMENT TO A PRODUCT
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addComment(@PathVariable productID: ObjectId,
                           @RequestBody @Validated commentDTO: CommentDTO): CommentDTO {
        return commentService.addComment(productID,commentDTO)
    }

    //UPDATE A COMMENT GIVEN ITS ID AND THE PRODUCT ID
    @PutMapping("/{commentID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateComment(@PathVariable productID: ObjectId,
                              @PathVariable commentID: ObjectId,
                              @RequestBody @Validated commentDTO: CommentDTO): CommentDTO {
        return commentService.updateComment(productID,commentID,commentDTO)
    }

    //DELETE A COMMENT GIVEN ITS ID AND THE PRODUCT ID
    @DeleteMapping("/{commentID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteComment(@PathVariable productID: ObjectId, @PathVariable commentID: ObjectId) {
        return commentService.deleteComment(productID,commentID)
    }
}
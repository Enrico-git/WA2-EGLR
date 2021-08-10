package it.polito.wa2.catalogservice.controllers

import it.polito.wa2.catalogservice.dto.CommentDTO
import it.polito.wa2.catalogservice.services.CommentService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@RestController
@RequestMapping("/products/{productID}/comments")
class CommentController(
    private val commentService: CommentService
) {
    val serviceURL = "http://localhost:8200"
    //Create a WebClient instance
    //building a client by using the DefaultWebClientBuilder class, which allows full customization
    val client: WebClient = WebClient.builder()
        .baseUrl(serviceURL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
        .defaultUriVariables(Collections.singletonMap("url", serviceURL))
        .build()

    //RETRIEVE INFO ABOUT A COMMENT GIVEN ITS ID
    @GetMapping("/{commentID}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getComment(@PathVariable commentID: String): CommentDTO {
        return commentService.getComment(commentID)
    }

    //CREATE A NEW COMMENT TO A PRODUCT
    @PostMapping("/{productID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addComment(@PathVariable productID: String,
                           @RequestBody @Validated commentDTO: CommentDTO): CommentDTO {
        return commentService.addComment(productID,commentDTO)
    }

    //UPDATE A COMMENT GIVEN ITS ID AND THE PRODUCT ID
    @PutMapping("/{productID}/{commentID}")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun updateComment(@PathVariable productID: String,
                              @PathVariable commentID: String,
                              @RequestBody @Validated commentDTO: CommentDTO): CommentDTO {
        return commentService.updateComment(productID,commentID,commentDTO)
    }

    //DELETE A COMMENT GIVEN ITS ID AND THE PRODUCT ID
    @DeleteMapping("/{productID}/{commentID}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteComment(@PathVariable productID: String, @PathVariable commentID: String) {
        return commentService.deleteComment(productID,commentID)
    }
}
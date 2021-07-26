package it.polito.wa2.warehouseservice.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.sql.Timestamp

@ControllerAdvice
class ControllerAdvice {
    @ExceptionHandler(value = [NotFoundException::class])
    fun genericExceptionHandler(e: Exception): ResponseEntity<ErrorDTO>{
        val errorDTO = ErrorDTO(
                timestamp = Timestamp(System.currentTimeMillis()),
                error = e.message!!
        )

        var status = HttpStatus.BAD_REQUEST
        when(e){
            is NotFoundException -> {
                errorDTO.status = 404
                status = HttpStatus.NOT_FOUND
            }
        }

        return ResponseEntity(errorDTO, status)
    }
}

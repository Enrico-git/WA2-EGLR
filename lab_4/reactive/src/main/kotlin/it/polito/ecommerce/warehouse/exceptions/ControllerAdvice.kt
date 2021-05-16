package it.polito.ecommerce.warehouse.exceptions

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice
import java.sql.Timestamp
import javax.validation.ValidationException

@ControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(value = [ValidationException::class, NotFoundException::class, IllegalArgumentException::class, OptimisticLockingFailureException::class])
    fun genericExceptionHandler(e: Exception): ResponseEntity<ErrorDTO> {
        val errorDTO = ErrorDTO(
            timestamp = Timestamp(System.currentTimeMillis()),
            error = e.message!!
        )

        var status = HttpStatus.BAD_REQUEST

        when (e) {
            is ValidationException -> {
                errorDTO.status = 422
                status = HttpStatus.UNPROCESSABLE_ENTITY
            }
            is OptimisticLockingFailureException -> {
                errorDTO.status = 500
                errorDTO.error = "Database concurrency error"
                status = HttpStatus.INTERNAL_SERVER_ERROR
            }
            is NotFoundException -> {
                errorDTO.status = 404
                status = HttpStatus.NOT_FOUND
            }
            is IllegalArgumentException -> {
                status = HttpStatus.BAD_REQUEST
            }
        }

        return ResponseEntity(errorDTO, status)
    }


    //    DTO validation in post request
//    @ExceptionHandler(value = [BindException::class])
//    fun bindExceptionHandler(e: BindException): ResponseEntity<ErrorDTO> {
//        val errorDTO = ErrorDTO(
//            timestamp = Timestamp(System.currentTimeMillis()),
//            error = e.fieldErrors
//                .map{"${it.field} - ${it.defaultMessage}"}
//                .reduce{acc, string -> "$acc; $string"},
//            status = 422
//        )
//        return ResponseEntity(errorDTO, HttpStatus.UNPROCESSABLE_ENTITY)
//    }
}

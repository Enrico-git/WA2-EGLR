package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.ErrorDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.services.WalletService
import javassist.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.sql.Timestamp
import javax.validation.Valid
import javax.validation.ValidationException
import javax.validation.constraints.Min

@RestController
@RequestMapping("/wallet") //or WalletServiceImpl
@Validated
class WalletController(private val service: WalletService) {

    @ExceptionHandler(value = [NotFoundException::class,IllegalArgumentException::class,ValidationException::class])
    fun exceptionHandler(e: Exception): ResponseEntity<ErrorDTO>{
        var errorDTO=ErrorDTO(
            timestamp = Timestamp(System.currentTimeMillis()),
            error = e.message!!
        )
        var status = HttpStatus.BAD_REQUEST
        when(e) {
            is ValidationException -> {
                errorDTO.status=422
                errorDTO.error = errorDTO.error.substringAfter(".")
                status=HttpStatus.UNPROCESSABLE_ENTITY
            }
            is NotFoundException -> {
                errorDTO.status=404
                status=HttpStatus.NOT_FOUND
            }
            is IllegalArgumentException -> {
                errorDTO.status=400
            }
        }
        return ResponseEntity(errorDTO,status)
    }

    @ExceptionHandler(value = [BindException::class])
    fun bindExceptionHandler(e: BindException): ResponseEntity<ErrorDTO>{
        val errorDTO = ErrorDTO(
            timestamp = Timestamp(System.currentTimeMillis()),
            error = e.fieldErrors
                .map{"${it.field} - ${it.defaultMessage}"}
                .reduce{acc, string -> "$acc; $string"},
            status = 422
        )

        return ResponseEntity(errorDTO, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @GetMapping("/{walletId}")
    fun getWallet(@PathVariable @Min(0, message="the wallet ID must be higher than 0")  walletId: Long): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.getWallet(walletId),HttpStatus.OK)
    }

    @PostMapping("/")
    fun createWallet(@RequestBody @Valid customer: CustomerDTO): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.addWallet(customer),HttpStatus.CREATED)
    }

    @PostMapping("/{walletId}/transaction")
    fun createTransaction(@PathVariable @Min(0, message="the wallet ID must be higher than 0") walletId: Long,
                          @RequestBody @Valid transactionDTO: TransactionDTO): ResponseEntity<TransactionDTO> {
        transactionDTO.senderID=walletId
        return ResponseEntity(service.performTransaction(transactionDTO),HttpStatus.CREATED)
    }

    @GetMapping("/{walletId}/transactions")
    fun getWalletTransactions(@PathVariable @Min(0, message="the wallet ID must be higher than 0") walletId: Long,
                              @RequestParam from: Long?,
                              @RequestParam to: Long?): ResponseEntity<List<TransactionDTO>> {
        return ResponseEntity(service.getWalletTransactions(walletId,from,to),HttpStatus.OK)
    }

    @GetMapping("/{walletId}/transactions/{transactionId}")
    fun getWalletTransaction(@PathVariable @Min(0, message="the wallet ID must be higher than 0") walletId: Long,
                             @PathVariable @Min(0, message="the transaction ID must be higher than 0") transactionId: Long): ResponseEntity<TransactionDTO> {
        return ResponseEntity(service.getWalletTransaction(walletId,transactionId),HttpStatus.OK)
    }
}

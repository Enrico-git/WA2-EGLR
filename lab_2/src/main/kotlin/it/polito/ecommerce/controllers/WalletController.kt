package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.*
import it.polito.ecommerce.services.WalletService
import javassist.NotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import javax.validation.Valid
import javax.validation.ValidationException
import javax.validation.constraints.Min

@RestController
@RequestMapping("/wallet")
@Validated
class WalletController(private val service: WalletService) {

    @ExceptionHandler(value = [NotFoundException::class, IllegalArgumentException::class, ValidationException::class])
    fun exceptionHandler(e: Exception): ResponseEntity<ErrorDTO>{
        val errorDTO = ErrorDTO(
            timestamp = Timestamp(System.currentTimeMillis()),
            error = e.message!!
        )

        var status = HttpStatus.BAD_REQUEST

        when(e){
            is ValidationException -> {
                errorDTO.status = 422
                errorDTO.error = errorDTO.error.replace(Regex("\\w+\\."), "")
                status = HttpStatus.UNPROCESSABLE_ENTITY
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


    @GetMapping("/{walletID}")
    fun getWallet(@PathVariable @Min(0, message="The wallet ID must be higher than 0") walletID: Long): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.getWallet(walletID), HttpStatus.OK)
    }

    @PostMapping("/")
    fun createWallet(@RequestBody @Valid customerDTO: CustomerDTO): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.addWallet(customerDTO), HttpStatus.CREATED)
    }

    @PostMapping("/{walletID}/transaction")
    fun createTransaction(@PathVariable @Min(0, message="The wallet ID must be higher than 0") walletID: Long,
                          @RequestBody @Valid transactionDTO: TransactionDTO): ResponseEntity<TransactionDTO>
    {
        transactionDTO.senderID = walletID
        return ResponseEntity(service.performTransaction(transactionDTO), HttpStatus.CREATED)
    }

    @GetMapping("/{walletID}/transactions")
    fun getWalletTransactions(@PathVariable @Min(0, message = "The wallet ID must be higher than 0") walletID: Long,
                              @RequestParam from: Long?,
                              @RequestParam to: Long?,
                              pageable: Pageable
    ): ResponseEntity<List<TransactionDTO>> {
        return ResponseEntity(service.getWalletTransactions(walletID, from, to, pageable), HttpStatus.OK)
    }

    @GetMapping("/{walletID}/transactions/{transactionID}")
    fun getWalletTransaction(@PathVariable @Min(0, message = "The wallet ID must be higher than 0") walletID: Long,
                             @PathVariable @Min(0, message = "The transaction ID must be higher than 0") transactionID: Long
    ): ResponseEntity<TransactionDTO> {
        return ResponseEntity(service.getWalletTransaction(walletID, transactionID), HttpStatus.OK)
    }
}
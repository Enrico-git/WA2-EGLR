package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.*
import it.polito.ecommerce.services.WalletService
import javassist.NotFoundException
import org.hibernate.exception.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
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
//         TODO VALIDATION ERROR CUSTOM MSG
        when(e){
            is ValidationException -> {
                errorDTO.status = 422
                status = HttpStatus.UNPROCESSABLE_ENTITY
            }
            is NotFoundException -> {
                errorDTO.status = 404
                status = HttpStatus.NOT_FOUND
            }
            is IllegalArgumentException -> {
                errorDTO.status = 400
                status = HttpStatus.BAD_REQUEST
            }
        }

        return ResponseEntity(errorDTO, status)
    }

    @GetMapping("/{id}")
    fun getWallet(@PathVariable @Min(0) id: Long): ResponseEntity<WalletDTO> {
        return ResponseEntity<WalletDTO>(service.getWallet(id), HttpStatus.OK)
    }

    @PostMapping("/")
    fun createWallet(@RequestBody @Valid customerDTO: CustomerDTO, bindingResult: BindingResult): ResponseEntity<WalletDTO> {
        if (bindingResult.hasErrors())
            throw ValidationException("Validation error")
        return ResponseEntity(service.addWallet(customerDTO.id), HttpStatus.CREATED)
    }

    @PostMapping("/{walletID}/transaction")
    fun createTransaction(@PathVariable @Min(0) walletID: Long, receiverID: Long, @Min(0) amount: BigDecimal): ResponseEntity<TransactionDTO> {
        return ResponseEntity<TransactionDTO>(service.performTransaction(walletID, receiverID, amount), HttpStatus.CREATED)
    }

    @GetMapping("/{walletID}/transactions")
    fun getWalletTransactions(@PathVariable walletID: Long,
                              @RequestParam from: Long?,
                              @RequestParam to: Long?): ResponseEntity<List<TransactionDTO>> {
        // format of from and to: 2021-03-31T13:58:35.000000000
// HTTP GET localhost:8080/wallet..../transactions?from=2021-03-31T13:58:35.000000000&to=2021-04-31T13:58:35.000000000
//        try {
            return ResponseEntity(service.getWalletTransactions(walletID, from, to), HttpStatus.OK)
//        } catch (e: IllegalArgumentException){ //Make sure emtpy list throws exception TODO
//            println(e)
//        }
//        return null
    }
}
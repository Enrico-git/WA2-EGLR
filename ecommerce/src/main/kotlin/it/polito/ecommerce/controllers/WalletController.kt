package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.ErrorDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.services.WalletService
import javassist.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import javax.validation.Valid
import javax.validation.ValidationException
import javax.validation.constraints.Min

@RestController
@RequestMapping("/wallet")
@Validated
class WalletController(private val walletService: WalletService) {

    @ExceptionHandler(value = [NotFoundException::class, IllegalArgumentException::class, ValidationException::class])
    fun exceptionHandler(e: Exception): ResponseEntity<ErrorDTO> {
        var status: Int = 400
        var message: String = e.message!!


        when (e){
            is ValidationException -> {
                status = 422
                message = "Illegal Customer ID"
            }

            is NotFoundException -> {
                status = 404
            }

            is IllegalAccessException -> {
                status = 400
            }
        }

        val errorDTO = ErrorDTO(
            Timestamp(System.currentTimeMillis()),
            status,
            message)

        return ResponseEntity(errorDTO,
                            HttpStatus.BAD_REQUEST)
    }

    /*
    /wallet [POST] à Create a new wallet for a given customer. In the Request Body there will be the
    Customer’s ID for which you want to create a wallet. The wallet created will initially have no money.
    Once the wallet is created, return a 201 (created) response status and the wallet itself as the
    response body
     */


    data class BohDTO(
        @field:Min(0) val customerID: Long
    )

    @PostMapping("/")
    fun createWallet(@RequestBody @Valid customer: CustomerDTO, bindingResult: BindingResult): ResponseEntity<WalletDTO>{
        println("test1")
        if(bindingResult.hasErrors()){
            println("test")
            throw ValidationException("Illegal Customer ID")
        }
        println("test2")
        return ResponseEntity(walletService.addWallet(customer.id), HttpStatus.CREATED)
    }

    /*
    /wallet/{walletId} [GET] à Get the details of a wallet. The response body will be the requested
    wallet, and the response status 200 (ok)
    */
    //fun getWallet()


}
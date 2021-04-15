package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.ErrorDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
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
class WalletController(private val walletService: WalletService) {
    /*
        /wallet [POST] à Create a new wallet for a given customer. In the Request Body there will be the
        Customer’s ID for which you want to create a wallet. The wallet created will initially have no money.
        Once the wallet is created, return a 201 (created) response status and the wallet itself as the
        response body
     */

    @PostMapping("/")
    fun createWallet(
        @RequestBody @Valid customer: CustomerDTO
    ): ResponseEntity<WalletDTO>{
        return ResponseEntity(walletService.addWallet(customer), HttpStatus.CREATED)
    }

    /*
        /wallet/{walletId} [GET] à Get the details of a wallet. The response body will be the requested
        wallet, and the response status 200 (ok)
    */
    @GetMapping("/{walletID}")
    fun getWallet(
        @PathVariable @Min(0, message = "the wallet ID must be higher than zero") walletID: Long
    ): ResponseEntity<WalletDTO>{
        return ResponseEntity(walletService.getWallet(walletID), HttpStatus.OK)
    }

    /*
        /wallet/{walletId}/transaction [POST] à Create a transaction taking the amount of money set in the
        body from the given wallet and transferring it to a second walletId, always defined in the body.
        Return the created transaction
     */

    @PostMapping("/{walletID}/transaction")
    fun createTransaction(
        @PathVariable @Min(0, message = "the wallet (sender) ID must be higher than zero") walletID: Long,
        @RequestBody @Valid transactionDTO: TransactionDTO
    ) : ResponseEntity<TransactionDTO>{
        transactionDTO.senderID = walletID
        return ResponseEntity(walletService.performTransaction(transactionDTO), HttpStatus.CREATED)
    }

    /*
        /wallet/{walletId}/transactions?from=<dateInMillis>&to=<dateInMillis> [GET] à Get a list of
        transactions regarding a given wallet in a given time frame
     */
    @GetMapping("/{walletID}/transactions")
    fun getWalletTransactions(@PathVariable @Min(0, message = "The wallet ID must be higher than 0") walletID: Long,
                              @RequestParam from: Long?,
                              @RequestParam to: Long?,
                              pageable: Pageable
    ): ResponseEntity<List<TransactionDTO>> {
        return ResponseEntity(walletService.getWalletTransactions(walletID, from, to, pageable), HttpStatus.OK)
    }


    /*
     /wallet/{walletId}/transactions/{transactionId} [GET] à Get the details of a single transaction
    */

    @GetMapping("{walletID}/transactions/{transactionID}")
    fun getWalletTransaction(@PathVariable @Min(0, message = "The wallet ID must be higher than 0") walletID: Long,
                            @PathVariable @Min(0, message = "The transaction ID must be higher than 0") transactionID: Long
    ): ResponseEntity<TransactionDTO>{
        return ResponseEntity(walletService.getWalletTransaction(walletID, transactionID), HttpStatus.OK)
    }


}
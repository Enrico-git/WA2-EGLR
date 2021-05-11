package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.*
import it.polito.ecommerce.services.WalletService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping("/wallet")
@Validated
class WalletController(private val service: WalletService) {

    @GetMapping("/{walletID}")
    fun getWallet(@PathVariable @Min(0) walletID: Long): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.getWallet(walletID), HttpStatus.OK)
    }

    @PostMapping("/")
    fun createWallet(@RequestBody @Valid createWalletDTO: CreateWalletDTO): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.addWallet(createWalletDTO), HttpStatus.CREATED)
    }

    @PostMapping("/{walletID}/transaction")
    fun createTransaction(
        @PathVariable @Min(0) walletID: Long,
        @RequestBody @Valid transactionDTO: TransactionDTO
    ): ResponseEntity<TransactionDTO> {
        transactionDTO.senderID = walletID
        return ResponseEntity(service.performTransaction(transactionDTO), HttpStatus.CREATED)
    }

    @GetMapping("/{walletID}/transactions")
    fun getWalletTransactions(
        @PathVariable @Min(0) walletID: Long,
        @RequestParam from: Long?,
        @RequestParam to: Long?,
        pageable: Pageable
    ): ResponseEntity<List<TransactionDTO>> {
        return ResponseEntity(service.getWalletTransactions(walletID, from, to, pageable), HttpStatus.OK)
    }

    @GetMapping("/{walletID}/transactions/{transactionID}")
    fun getWalletTransaction(
        @PathVariable @Min(0) walletID: Long,
        @PathVariable @Min(0) transactionID: Long
    ): ResponseEntity<TransactionDTO> {
        return ResponseEntity(service.getWalletSingleTransaction(walletID, transactionID), HttpStatus.OK)
    }

}
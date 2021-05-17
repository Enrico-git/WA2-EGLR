package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.CustomerDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
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

    @GetMapping("/{walletId}")
    fun getWallet(@PathVariable @Min(0) walletId: Long): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.getWallet(walletId), HttpStatus.OK)
    }

    @PostMapping("/")
    fun createWallet(@RequestBody @Valid customer: CustomerDTO): ResponseEntity<WalletDTO> {
        return ResponseEntity(service.addWallet(customer), HttpStatus.CREATED)
    }

    @PostMapping("/{walletId}/transaction")
    fun createTransaction(
        @PathVariable @Min(0) walletId: Long,
        @RequestBody @Valid transactionDTO: TransactionDTO
    ): ResponseEntity<TransactionDTO> {
        transactionDTO.senderID = walletId
        return ResponseEntity(service.performTransaction(transactionDTO), HttpStatus.CREATED)
    }

    @GetMapping("/{walletId}/transactions")
    fun getWalletTransactions(
        @PathVariable @Min(0) walletId: Long,
        @RequestParam from: Long?,
        @RequestParam to: Long?,
        pageable: Pageable
    ): ResponseEntity<List<TransactionDTO>> {
        return ResponseEntity(service.getWalletTransactions(walletId, from, to, pageable), HttpStatus.OK)
    }

    @GetMapping("/{walletId}/transactions/{transactionId}")
    fun getWalletTransaction(
        @PathVariable @Min(0) walletId: Long,
        @PathVariable @Min(0) transactionId: Long
    ): ResponseEntity<TransactionDTO> {
        return ResponseEntity(service.getWalletSingleTransaction(walletId, transactionId), HttpStatus.OK)
    }
}

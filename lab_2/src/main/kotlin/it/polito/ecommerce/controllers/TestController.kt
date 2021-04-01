package it.polito.ecommerce.controllers

import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.services.WalletServiceImpl
import javassist.NotFoundException
import org.springframework.dao.DataAccessException
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.format.DateTimeFormatter
import javax.validation.constraints.Min

@RestController
class TestController(private val service: WalletServiceImpl) {
    @GetMapping("/wallet/{id}")
    fun getWallet(@PathVariable id: Int): WalletDTO? {
        try {
            return service.getWallet(id)
        } catch (e: NotFoundException) {
            println(e)
        }
        return null
    }

    @PostMapping("/wallet")
    fun createWallet(customerID: Int): WalletDTO? {
        try {
            return service.addWallet(customerID)
        } catch (e: DataAccessException){
            println(e)
        }
        return null
    }

    @PostMapping("/wallet/{walletID}/transaction")
    fun createTransaction(@PathVariable walletID: Int, receiverID: Int, @Min(0) amount: Double): TransactionDTO? {
        try {
            return service.performTransaction(walletID, receiverID, amount)
        } catch (e: Exception){ //Make proper exception
            println(e)
        }
        return null
    }

    @GetMapping("/wallet/{walletID}/transactions")
    fun getWalletTransactions(@PathVariable walletID: Int,
                              @RequestParam from: Long?,
                              @RequestParam to: Long?): List<TransactionDTO>? {
        // format of from and to: 2021-03-31T13:58:35.000000000
// HTTP GET localhost:8080/wallet..../transactions?from=2021-03-31T13:58:35.000000000&to=2021-04-31T13:58:35.000000000
        try {
            return service.getWalletTransactions(walletID, from, to)
        } catch (e: Exception){ //Make sure emtpy list throws exception TODO
            println(e)
        }
        return null
    }
}
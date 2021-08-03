package it.polito.wa2.walletservice.routers

import it.polito.wa2.walletservice.dto.TransactionDTO
import it.polito.wa2.walletservice.dto.WalletDTO
import it.polito.wa2.walletservice.services.WalletService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*


@Component
class WalletHandler(
    private val walletService: WalletService
) {
    /**
     * coroutine uses ServerResponse instead of Flux and Mono.
     * https://blog.frankel.ch/reactor-to-coroutines/
     */

    suspend fun getWallet(request: ServerRequest): ServerResponse {
        val walletID = request.pathVariable("walletID")
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(walletService.getWallet(walletID))
    }

    suspend fun createWallet(request: ServerRequest): ServerResponse {
        val walletDTO = request.awaitBody(WalletDTO::class)
            return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(walletService.createWallet(walletDTO))
    }

    /**
     * Please notice that the wallet must have some money (balance)
     * So the customer has to **recharge** the wallet.
     * For doing so, the admin calls this end-point.
     * For *Payment* or *Refund* (compensative transaction) it will be used Kafka instead.
     */
    suspend fun createTransaction(request: ServerRequest): ServerResponse{
        val walletID = request.pathVariable("walletID")
        val transactionDTO = request.awaitBody(TransactionDTO::class)
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(walletService.createRechargeTransaction(walletID, transactionDTO))
    }

    suspend fun getAllTransactions(request: ServerRequest): ServerResponse{
        val walletID = request.pathVariable("walletID")
        val from = request.queryParamOrNull("from")?.toLong()
        val to = request.queryParamOrNull("to")?.toLong()

        val page = request.queryParamOrNull("page")?.toInt()
        val size = request.queryParamOrNull("size")?.toInt()
        var pageable: Pageable = Pageable.unpaged()
        if(page != null && size != null)
            pageable = PageRequest.of(page, size)

        return ServerResponse
            .ok()
            .json()
            .bodyAndAwait(walletService.getAllTransactions(walletID, from, to, pageable))
    }

    suspend fun getTransaction(request: ServerRequest): ServerResponse{
        val walletID = request.pathVariable("walletID")
        val transactionID = request.pathVariable("transactionID")
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(walletService.getTransaction(walletID, transactionID))
    }

    suspend fun mockPaymentOrAbortRequest(request: ServerRequest): ServerResponse{
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(walletService.mockPaymentOrAbortRequest())
    }
}

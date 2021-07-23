package it.polito.wa2.wallet.routers

import it.polito.wa2.wallet.dto.TransactionDTO
import it.polito.wa2.wallet.entities.Wallet
import it.polito.wa2.wallet.services.WalletService
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

        //{"id":{"timestamp":1626763353,"date":"2021-07-20T06:42:33.000+00:00"},"balance":32.13,"userID":{"timestamp":1626763221,"date":"2021-07-20T06:40:21.000+00:00"}}
        //TODO understand Why ObjectID is mapped as {timestamp, date}
    }

    suspend fun createWallet(request: ServerRequest): ServerResponse {
        val wallet = request.awaitBody(Wallet::class)
            return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(walletService.createWallet(wallet))
    }

    suspend fun createTransaction(request: ServerRequest): ServerResponse{
        val walletID = request.pathVariable("walletID")
        val transactionDTO = request.awaitBody(TransactionDTO::class)

        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait(walletService.createTransaction(walletID, transactionDTO))
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

    suspend fun getWalletTransaction(request: ServerRequest): ServerResponse{
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait("walletService.getWalletTransaction(walletID)")
    }
}

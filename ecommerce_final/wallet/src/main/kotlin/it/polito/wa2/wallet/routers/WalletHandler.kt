package it.polito.wa2.wallet.routers

import it.polito.wa2.wallet.entities.Wallet
import it.polito.wa2.wallet.services.WalletService
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
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait("walletService.createTransaction(walletID)")
    }

    suspend fun getWalletTransactions(request: ServerRequest): ServerResponse{
        println("GET WALLET TRANSACITONS")
        // http://localhost:8100/wallets/60f53679ff7b674f90c399a9/transactions?from=123123&to=456456
        val walletID = request.pathVariable("walletID")

        println("walletID: ${walletID}")
        println("from: ${request.queryParam("from")}")
        println("to: ${request.queryParam("to")}")

        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait("walletService.getWalletTransactions(walletID)")
    }

    suspend fun getWalletTransaction(request: ServerRequest): ServerResponse{
        return ServerResponse
            .ok()
            .json()
            .bodyValueAndAwait("walletService.getWalletTransaction(walletID)")
    }
}

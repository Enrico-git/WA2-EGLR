package it.polito.wa2.wallet.services

import it.polito.wa2.wallet.dto.WalletDTO
import org.bson.types.ObjectId
import reactor.core.publisher.Mono

interface WalletService {
    suspend fun getWallet(walletID: String): WalletDTO

}

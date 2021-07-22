package it.polito.wa2.wallet.services

import it.polito.wa2.wallet.dto.WalletDTO
import it.polito.wa2.wallet.entities.Wallet
import org.springframework.security.access.prepost.PreAuthorize

interface WalletService {
    @PreAuthorize("hasAuthority(\"CUSTOMER\")") //TODO or ADMIN
    suspend fun getWallet(walletID: String): WalletDTO

    @PreAuthorize("hasAuthority(\"ADMIN\")")
    suspend fun createWallet(wallet: Wallet): WalletDTO

}

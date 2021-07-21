package it.polito.wa2.wallet.services

import it.polito.wa2.wallet.dto.WalletDTO
import org.springframework.security.access.prepost.PreAuthorize

interface WalletService {
    @PreAuthorize("hasAuthority(\"CUSTOMER\")") //TODO does it work? try filter in router
    suspend fun getWallet(walletID: String): WalletDTO

}

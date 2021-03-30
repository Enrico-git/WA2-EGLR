package it.polito.ecommerce.services

import it.polito.ecommerce.dto.WalletDTO
import it.polito.ecommerce.repositories.WalletRepository
import org.springframework.stereotype.Service

@Service
class WalletServiceImpl(private val walletRepository: WalletRepository): WalletService {
    override fun getWallet(walletID: Int): WalletDTO? {
        val walletOpt = walletRepository.findById(walletID)
        val wallet = if(walletOpt.isPresent) walletOpt.get() else null
        if(wallet != null)
            return WalletDTO(wallet.balance)
        else
            return null
    }
}
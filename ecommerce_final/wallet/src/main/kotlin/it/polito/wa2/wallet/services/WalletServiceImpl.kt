package it.polito.wa2.wallet.services

import it.polito.wa2.wallet.dto.WalletDTO
import it.polito.wa2.wallet.entities.Wallet
import it.polito.wa2.wallet.entities.toDTO
import it.polito.wa2.wallet.exceptions.NotFoundException
import it.polito.wa2.wallet.repositories.WalletRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WalletServiceImpl(
        private val walletRepository: WalletRepository
    ) : WalletService {
    override suspend fun getWallet(walletID: String): WalletDTO {
        val wallet = walletRepository.findById(ObjectId(walletID)) ?: throw NotFoundException("Product was not found")
        return wallet.toDTO()
    }

    override suspend fun createWallet(wallet: Wallet): WalletDTO {
        //TODO Catalog (the only one who can access usersDB) has to check that user exists!

        return walletRepository.save(wallet).toDTO()
    }
}

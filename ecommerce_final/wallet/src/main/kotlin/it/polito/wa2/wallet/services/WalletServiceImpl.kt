package it.polito.wa2.wallet.services

import it.polito.wa2.wallet.dto.TransactionDTO
import it.polito.wa2.wallet.dto.WalletDTO
import it.polito.wa2.wallet.dto.toEntity
import it.polito.wa2.wallet.entities.toDTO
import it.polito.wa2.wallet.exceptions.NotFoundException
import it.polito.wa2.wallet.repositories.TransactionRepository
import it.polito.wa2.wallet.repositories.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
@Transactional
class WalletServiceImpl(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : WalletService {
    override suspend fun getWallet(walletID: String): WalletDTO {
        val wallet = walletRepository.findById(ObjectId(walletID)) ?: throw NotFoundException("Wallet was not found")
        return wallet.toDTO()
    }

    override suspend fun createWallet(walletDTO: WalletDTO): WalletDTO {
        //TODO Catalog (the only one who can access usersDB) has to check that user exists!
        return walletRepository.save(walletDTO.toEntity()).toDTO()
    }

    override suspend fun createTransaction(walletID: String, transactionDTO: TransactionDTO): TransactionDTO {
        //TODO Catalog has to check user existence (I suppose admin create wallet)
        //TODO check wallet
        //TODO check balance
        //TODO only admin can do refund

        transactionDTO.walletID = walletID
        transactionDTO.timestamp = Timestamp(System.currentTimeMillis()) //TODO Why faking two hours ago?

        return transactionRepository.save(transactionDTO.toEntity()).toDTO()
        //TODO insert the new transaction in wallet.
    }

    override suspend fun getAllTransactions(walletID: String, from: Long?,
                                            to: Long?, pageable: Pageable ) : Flow<TransactionDTO> {
        val walletIdObjectId = ObjectId(walletID)

        walletRepository.findById(walletIdObjectId) ?: throw NotFoundException("Wallet was not found")

        if ( from != null && to != null) {  //get all transactions (window)
            return transactionRepository
                .findAllByWalletIDAndTimestampBetween(walletIdObjectId, Timestamp(from),
                    Timestamp(to), pageable).map { it.toDTO() }
        }

        if ( from != null || to != null) // if exists both must be present
            throw IllegalArgumentException("Invalid parameters")

        // both 'from' and 'to' not present. (no window)
        return transactionRepository.findAllByWalletID(walletIdObjectId, pageable).map{it.toDTO()}
        // TODO in my pc timestamp printed here and in WSL2 are different of 2h. In mongodb are also 2h ago.
    }
}

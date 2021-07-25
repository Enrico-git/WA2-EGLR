package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.dto.TransactionDTO
import it.polito.wa2.walletservice.dto.UserDetailsDTO
import it.polito.wa2.walletservice.dto.WalletDTO
import it.polito.wa2.walletservice.dto.toEntity
import it.polito.wa2.walletservice.entities.toDTO
import it.polito.wa2.walletservice.exceptions.NotFoundException
import it.polito.wa2.walletservice.exceptions.UnauthorizedException
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.types.ObjectId
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
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
        val wallet = walletRepository.findById(ObjectId(walletID)) ?: throw NotFoundException("Wallet was not found")
        if(transactionDTO.amount <= BigDecimal(0))
            throw IllegalArgumentException("The amount for recharges must be greater than zero")

//        val auth = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication //JWT from Catalog
//        val user = auth.principal as UserDetailsDTO
//        val token = auth.credentials as String
//        if ( wallet.userID != user.id ) // the admin would recharge a user which is not the owner of the wallet
//            throw UnauthorizedException("Forbidden: The user is not the owner of the wallet")

        transactionDTO.walletID = walletID
        transactionDTO.timestamp = Timestamp(System.currentTimeMillis()) //TODO Why faking two hours ago?

        wallet.balance += transactionDTO.amount
        walletRepository.save(wallet)

        return transactionRepository.save(transactionDTO.toEntity()).toDTO()
        // I'm not inserting the new transaction in wallet since i assume, they use specific end-point for retrieve transactions
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

    override suspend fun getTransaction(walletID: String, transactionID: String): TransactionDTO {
        walletRepository.findById(ObjectId(walletID)) ?: throw NotFoundException("Wallet was not found")

        val transaction = transactionRepository.findById(ObjectId(transactionID)) ?: throw NotFoundException("Transaction was not found")
        return transaction.toDTO()
    }
}

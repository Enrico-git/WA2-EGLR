package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.dto.*
import it.polito.wa2.walletservice.entities.Transaction
import it.polito.wa2.walletservice.common.TransactionDescription
import it.polito.wa2.walletservice.entities.Wallet
import it.polito.wa2.walletservice.entities.toDTO
import it.polito.wa2.walletservice.exceptions.NotFoundException
import it.polito.wa2.walletservice.exceptions.UnauthorizedException
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import it.polito.wa2.walletservice.security.JwtUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.reactive.awaitSingle
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
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
    private val transactionRepository: TransactionRepository,
    private val jwtUtils: JwtUtils,
) : WalletService {

    @Value("\${spring.kafka.retryDelay}")
    private val retryDelay: Long = 0

    private suspend fun getWalletIfAuthorized(walletID: String, ex: Exception): Wallet {
        val wallet = walletRepository.findById(ObjectId(walletID)) ?: throw ex
        val userDetailsDTO = ReactiveSecurityContextHolder
            .getContext()
            .awaitSingle()
            .authentication
            .principal as UserDetailsDTO

        if ((userDetailsDTO.roles?.contains("ADMIN") == false)
            && (wallet.userID != userDetailsDTO.id) )
            throw UnauthorizedException("You are not the the owner of the wallet")
        return wallet
    }

    override suspend fun getWallet(walletID: String): WalletDTO {
        val wallet = getWalletIfAuthorized(walletID, NotFoundException("Wallet was not found"))
        return wallet.toDTO()
    }

    override suspend fun createWallet(walletDTO: WalletDTO): WalletDTO {
        return walletRepository.save(walletDTO.toEntity()).toDTO()
    }


    override suspend fun createRechargeTransaction(walletID: String, transactionDTO: TransactionDTO): TransactionDTO {
        val wallet = getWalletIfAuthorized(walletID, IllegalArgumentException("Wallet not found"))

        if(transactionDTO.amount <= BigDecimal(0))
            throw IllegalArgumentException("The amount for recharges must be greater than zero")

        transactionDTO.walletID = walletID
        transactionDTO.timestamp = Timestamp(System.currentTimeMillis())
        transactionDTO.description = TransactionDescription.RECHARGE.toString()
        transactionDTO.reason = "RECHARGE"
        wallet.balance += transactionDTO.amount
        walletRepository.save(wallet)

        return transactionRepository.save(transactionDTO.toEntity()).toDTO()
    }

    override suspend fun getAllTransactions(walletID: String, from: Long?,
                                            to: Long?, pageable: Pageable ) : Flow<TransactionDTO> {
        getWalletIfAuthorized(walletID,IllegalArgumentException("Wallet not found"))

        if ( from != null && to != null) {  //get all transactions (window)
            return transactionRepository
                .findAllByWalletIDAndTimestampBetween( ObjectId(walletID), Timestamp(from),
                    Timestamp(to), pageable).map { it.toDTO() }
        }

        if ( from != null || to != null) // if exists both must be present
            throw IllegalArgumentException("Invalid parameters")

        return transactionRepository.findAllByWalletID(ObjectId(walletID), pageable).map{it.toDTO()}
    }

    override suspend fun getTransaction(walletID: String, transactionID: String): TransactionDTO {
        getWalletIfAuthorized(walletID, IllegalArgumentException("Wallet not found"))

        val transaction = transactionRepository.findById(ObjectId(transactionID)) ?: throw NotFoundException("Transaction was not found")
        return transaction.toDTO()
    }

    suspend fun getAuthorizedUser(token: String): UserDetailsDTO?{
        val user = if (jwtUtils.validateJwtToken(token))
            jwtUtils.getDetailsFromJwtToken(token)
        else return null

        if (!(user.roles?.contains("CUSTOMER") == true || user.roles?.contains("ADMIN") == true))
            return null
        return user
    }

    override suspend fun createPaymentTransaction(paymentRequestDTO: KafkaPaymentOrRefundRequestDTO): Boolean? {
        if (Timestamp(paymentRequestDTO.timestamp.time + retryDelay) < Timestamp(System.currentTimeMillis()))
            return false

        val user = getAuthorizedUser(paymentRequestDTO.token) ?: return false

        val transactionDescription = TransactionDescription.PAYMENT

        if (transactionRepository.findByReasonAndDescription(ObjectId(paymentRequestDTO.orderID), transactionDescription) != null)
                return null


        val wallet = walletRepository.findByUserID(user.id) ?: return false

        if (wallet.balance >= paymentRequestDTO.amount)
            wallet.balance -= paymentRequestDTO.amount
        else
            return false

        walletRepository.save(wallet)

        transactionRepository.save(
            Transaction(
                id = null,
                timestamp = Timestamp(System.currentTimeMillis()),
                walletID = wallet.id!!,
                amount = paymentRequestDTO.amount,
                description = transactionDescription,
                reason = paymentRequestDTO.orderID
            )
        )

        return true
    }

    override suspend fun createRefundTransaction(abortPaymentRequestDTO: KafkaPaymentOrRefundRequestDTO): Boolean? {
        if (Timestamp(abortPaymentRequestDTO.timestamp.time + retryDelay) < Timestamp(System.currentTimeMillis()))
            return false

        val user = getAuthorizedUser(abortPaymentRequestDTO.token) ?: return false

        val transactions = transactionRepository.findAllByReason(ObjectId(abortPaymentRequestDTO.orderID)).toSet()
        if (transactions.any{it.description == TransactionDescription.REFUND})
            return null

        val transaction = transactions.find{it.description == TransactionDescription.PAYMENT} ?: return false

        val wallet = walletRepository.findById(transaction.walletID) ?: return false

        if ( ! user.roles!!.contains("ADMIN") && wallet.userID != user.id)
            return false

        wallet.balance += transaction.amount

        walletRepository.save(wallet)


        transactionRepository.save(
            Transaction(
                id = null,
                timestamp = Timestamp(System.currentTimeMillis()),
                walletID = wallet.id!!,
                amount = abortPaymentRequestDTO.amount,
                description = TransactionDescription.REFUND,
                reason = abortPaymentRequestDTO.orderID
            )
        )

        return true
    }
}

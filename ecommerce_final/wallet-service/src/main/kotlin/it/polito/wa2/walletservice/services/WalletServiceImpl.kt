package it.polito.wa2.walletservice.services

import it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO
import it.polito.wa2.walletservice.dto.TransactionDTO
import it.polito.wa2.walletservice.dto.WalletDTO
import it.polito.wa2.walletservice.dto.toEntity
import it.polito.wa2.walletservice.entities.TransactionDescription
import it.polito.wa2.walletservice.entities.toDTO
import it.polito.wa2.walletservice.exceptions.NotFoundException
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import it.polito.wa2.walletservice.security.JwtUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.awaitFirst
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
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
    private val mockKafkaPaymentReqProducer: KafkaProducer<String, KafkaPaymentRequestDTO>,
) : WalletService {

    @Value("\${spring.kafka.retryDelay}")
    private val retryDelay: Long = 0

    override suspend fun getWallet(walletID: String): WalletDTO {
        val wallet = walletRepository.findById(ObjectId(walletID)) ?: throw NotFoundException("Wallet was not found")
        return wallet.toDTO()
    }

    override suspend fun createWallet(walletDTO: WalletDTO): WalletDTO {
        //TODO Catalog (the only one who can access usersDB) has to check that user exists!
        return walletRepository.save(walletDTO.toEntity()).toDTO()
    }

    /**
     * This is called by admin only (REST) for RECHARGE the wallet.
     */
    override suspend fun createRechargeTransaction(walletID: String, transactionDTO: TransactionDTO): TransactionDTO {
        val wallet = walletRepository.findById(ObjectId(walletID)) ?: throw NotFoundException("Wallet was not found")
        if(transactionDTO.amount <= BigDecimal(0))
            throw IllegalArgumentException("The amount for recharges must be greater than zero")

        //TODO Catalog has to check the userID in the JWT exists!
//        val auth = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication //JWT from Catalog
//        val user = auth.principal as UserDetailsDTO
//        if ( wallet.userID != user.id ) // the admin would recharge a user which is not the owner of the wallet
//            throw UnauthorizedException("Forbidden: The user is not the owner of the wallet")

        transactionDTO.walletID = walletID
        transactionDTO.timestamp = Timestamp(System.currentTimeMillis())
        transactionDTO.description = TransactionDescription.RECHARGE.toString()

        wallet.balance += transactionDTO.amount
        walletRepository.save(wallet)

        return transactionRepository.save(transactionDTO.toEntity()).toDTO()
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
    }

    override suspend fun getTransaction(walletID: String, transactionID: String): TransactionDTO {
        walletRepository.findById(ObjectId(walletID)) ?: throw NotFoundException("Wallet was not found")

        val transaction = transactionRepository.findById(ObjectId(transactionID)) ?: throw NotFoundException("Transaction was not found")
        return transaction.toDTO()
    }

    /**
     * this method starts when kafka lister of "request_payment" receive a new message
     * and returns "request_payment_failed" or "request_payment_ok" (debezium)
     */
    override suspend fun createPaymentOrRefundTransaction(topic: String, paymentRequestDTO: KafkaPaymentRequestDTO): Boolean? {
//         Avoid to process order already canceled by Order-Service
        if( Timestamp(paymentRequestDTO.timestamp.time + retryDelay) < Timestamp(System.currentTimeMillis()))
            return null

//         Checks that this request has not been served yet.
//         (Orders try 5 times, and maybe Wallet served it and then crashed)
//         in case of "request_failed" we don't have this information in DB
//         but order-service will handle it.
        val orderIDObj: ObjectId?
        try{
            orderIDObj = ObjectId(paymentRequestDTO.orderID)
        }
        catch (e: IllegalArgumentException){
            return false
        }

        val targetTransaction = transactionRepository.findByOrderID(orderIDObj)
        if(targetTransaction != null)
            return null

//         Security Checks
        val userDetailsDTO = jwtUtils.getDetailsFromJwtToken(paymentRequestDTO.token)
//        if (userDetailsDTO.id == null){
//            return false
//        }
//
//        if (!userDetailsDTO.isEnabled){
//            return false
//        }

        if ((userDetailsDTO.roles?.contains("CUSTOMER", true) != true) &&
            (userDetailsDTO.roles?.contains("ADMIN", true) != true)){
            return false
        }

//         UserID and Wallet checks
//        TODO The catalog has to check that the userID in JWT exists!
        val userIDObj: ObjectId?
        try{
            userIDObj = ObjectId("60f66fd598f6d22dc03092d4")
//                userIDObj = ObjectId(userDetailsDTO.id!!)
        }
        catch (e: IllegalArgumentException){
            return false
        }

        val wallet = walletRepository.findByUserID(userIDObj) ?: return false

        val description =
            when (topic){
                "payment_request" -> {
                    if ( (wallet.balance - paymentRequestDTO.amount) < BigDecimal(0) ) {
                        return false
                    }

                    wallet.balance -= paymentRequestDTO.amount

                    TransactionDescription.PAYMENT
                }
                "abort_payment_request" -> {
                    wallet.balance += paymentRequestDTO.amount
                    TransactionDescription.REFUND
                }
                else -> return false
            }

        walletRepository.save(wallet)

        val transactionDTO = TransactionDTO(
            id = null,
            timestamp = Timestamp(System.currentTimeMillis()),
            walletID = wallet.id!!.toHexString(),
            amount = paymentRequestDTO.amount,
            description = description.toString(),
            orderID = paymentRequestDTO.orderID
        )

//        This will trigger debezium that signals "payment_request_ok" or "abort_payment_request_ok"
        println(transactionRepository.save(transactionDTO.toEntity()))
        return true
    }

    /**
     * This method simulates what order-service will sent by means of kafka "payment_request" -> new_order
     * and simulates what order-service will sent by means of kafka "abort_payment_request" -> delete_order
     */
    override suspend fun mockPaymentOrAbortRequest(): String {
        val auth = ReactiveSecurityContextHolder.getContext().awaitFirst().authentication //JWT from Catalog
        val token = auth.credentials as String

        val mockPaymentOrAbortRequestDTO = KafkaPaymentRequestDTO(
            orderID = ObjectId().toHexString(),
            amount = BigDecimal(Math.random()*10%20),
            token = token,
            timestamp = Timestamp(System.currentTimeMillis())
        )

        println("MOCK: $mockPaymentOrAbortRequestDTO")

//        Use them in mutual exclusion:
//         or PAYMENT
//        mockKafkaPaymentReqProducer.send(ProducerRecord("payment_request", mockPaymentOrAbortRequestDTO))
//         or REFUND
        mockKafkaPaymentReqProducer.send(ProducerRecord("abort_payment_request", mockPaymentOrAbortRequestDTO))
        return "OK"
    }
}

package it.polito.wa2.walletservice.services.kafka

import it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO
import it.polito.wa2.walletservice.dto.TransactionDTO
import it.polito.wa2.walletservice.dto.toEntity
import it.polito.wa2.walletservice.entities.TransactionDescription
import it.polito.wa2.walletservice.repositories.TransactionRepository
import it.polito.wa2.walletservice.repositories.WalletRepository
import it.polito.wa2.walletservice.security.AuthenticationManager
import it.polito.wa2.walletservice.security.JwtUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrDefault
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.bson.types.ObjectId
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.sql.Timestamp

/**
 * This class is used for reading from "payment_request"
 * and insert in "payment_request_failed" topics.
 * It works as Service.
 * Moreover it's used for "abort_request_payment" and
 * "abort_request_payment_failed".
 * Just remember that "payment_request_ok" and "abort_payment_request_ok"
 * are automatically handled by debezium after the insert in
 * the relative collections.
 */
@Component
class KafkaPayment(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils,
    private val kafkaPaymentRequestFailedProducer: KafkaProducer<String, String>
) {
    @KafkaListener(
        topics=["payment_request"],
        containerFactory = "paymentRequestContainerFactory"
    )
    @Transactional
    fun paymentRequestConsumer(paymentRequestDTO: KafkaPaymentRequestDTO){
        CoroutineScope(Dispatchers.IO).launch {
            val authReq = UsernamePasswordAuthenticationToken(paymentRequestDTO.token, paymentRequestDTO.token)
            val auth = authenticationManager.authenticate(authReq).awaitFirst() //verify signature and return null or UserDetailsDTO

            val sc = SecurityContextHolder.getContext()
            ReactiveSecurityContextHolder.getContext().awaitFirstOrDefault(sc).authentication = auth

//            val userDetailsDTO = jwtUtils.getDetailsFromJwtToken(paymentRequestDTO.token)
//            if (userDetailsDTO.id == null){
//                kafkaPaymentRequestFailedProducer.send(
//                    ProducerRecord("payment_request_failed", paymentRequestDTO.orderID))
//                return@launch
//            }

            var userIDObj: ObjectId? = null
            try{
                userIDObj = ObjectId("60f66fd598f6d22dc03092d4")
//                userIDObj = ObjectId(userDetailsDTO.id!!)
            }
            catch (e: IllegalArgumentException){
                kafkaPaymentRequestFailedProducer.send(
                    ProducerRecord("payment_request_failed", paymentRequestDTO.orderID))
                return@launch
            }

            //TODO The catalog has to check that the userID in JWT exists!
            val wallet = walletRepository.findByUserID(userIDObj)

            if (wallet == null){
                kafkaPaymentRequestFailedProducer.send(
                    ProducerRecord("payment_request_failed", paymentRequestDTO.orderID))
                return@launch
            }

            val description =
                if (paymentRequestDTO.amount < BigDecimal(0))
                    TransactionDescription.PAYMENT
                else
                    TransactionDescription.REFUND

            if ( (description == TransactionDescription.PAYMENT)
                && ( wallet.balance < paymentRequestDTO.amount ) ){
                kafkaPaymentRequestFailedProducer.send(
                    ProducerRecord("payment_request_failed", paymentRequestDTO.orderID))
                return@launch
            }

            val transactionDTO = TransactionDTO(
                id = null,
                timestamp = Timestamp(System.currentTimeMillis()),
                walletID = wallet.id!!.toHexString(),
                amount = paymentRequestDTO.amount,
                description = description.toString(),
                orderID = paymentRequestDTO.orderID
            )

            wallet.balance += paymentRequestDTO.amount
            walletRepository.save(wallet)

            println("payment_request_ok: $transactionDTO")
            //This will trigger debezium that signals "payment_request_ok"
            transactionRepository.save(transactionDTO.toEntity())
        }
    }
}

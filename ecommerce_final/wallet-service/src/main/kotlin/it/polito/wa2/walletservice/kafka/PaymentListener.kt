package it.polito.wa2.walletservice.kafka

import it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO
import it.polito.wa2.walletservice.services.WalletService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

/**
 * This class is used for listening from "payment_request"
 * and for inserting in "payment_request_failed" topics.
 * It works as Service.
 * Moreover it's used for "abort_request_payment" and
 * "abort_request_payment_failed".
 * Just remember that "payment_request_ok" and "abort_payment_request_ok"
 * are automatically handled by debezium after the insert in
 * the relative collections.
 */
@Component
class PaymentListener(
    private val walletService: WalletService,
    private val kafkaPaymentRequestFailedProducer: KafkaProducer<String, String>
) {
    @KafkaListener(
        topics=["payment_request"],
        containerFactory = "paymentRequestContainerFactory"
    )
    fun paymentRequestConsumer(paymentRequestDTO: KafkaPaymentRequestDTO){
        CoroutineScope(Dispatchers.IO).launch {
            val result = walletService.createPaymentTransaction(paymentRequestDTO)

            if (result != null && result == false) {
                println("payment_request_failed")
                kafkaPaymentRequestFailedProducer.send(
                    ProducerRecord("payment_request_failed", paymentRequestDTO.orderID)
                )
            } // if the result == true, debezium will send "payment_request_ok"
        }
    }
}

package it.polito.wa2.walletservice.kafka

import it.polito.wa2.walletservice.dto.KafkaPaymentRequestDTO
import it.polito.wa2.walletservice.services.WalletService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

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
class PaymentOrAbortListener(
    private val walletService: WalletService,
    private val kafkaPaymentRequestFailedProducer: KafkaProducer<String, String>
) {
    @KafkaListener(
        topics=["payment_request", "abort_payment_request"],
        containerFactory = "paymentRequestContainerFactory"
    )
    fun requestConsumer(paymentOrAbortRequestDTO: KafkaPaymentRequestDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String){
        CoroutineScope(Dispatchers.IO).launch {
            val result = walletService.createPaymentOrRefundTransaction(topic, paymentOrAbortRequestDTO)

            if (result != null && result == false) {
                println("${topic}_failed")
                kafkaPaymentRequestFailedProducer.send(
                    ProducerRecord("${topic}_failed", paymentOrAbortRequestDTO.orderID)
                )
            } // if the result == true, debezium will send "payment_request_ok"
        }
    }
}

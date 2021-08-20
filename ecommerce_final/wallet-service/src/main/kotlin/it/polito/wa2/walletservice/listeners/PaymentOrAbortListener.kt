package it.polito.wa2.walletservice.listeners

import it.polito.wa2.walletservice.dto.KafkaPaymentOrRefundRequestDTO
import it.polito.wa2.walletservice.services.WalletService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.data.mongodb.UncategorizedMongoDbException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * This class is used for listening to "payment_request"
 * and to send messages to "payment_request_failed" topics.
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
        topics=["payment_request"],
        containerFactory = "paymentOrRefundRequestContainerFactory"
    )
    fun paymentRequestConsumer(paymentRequestDTO: KafkaPaymentOrRefundRequestDTO){
        CoroutineScope(Dispatchers.IO).launch {
            var counter = 5
            var result : Boolean? = false
            while (counter-- > 0)
                try {
                    result = walletService.createPaymentTransaction(paymentRequestDTO)
                    break
                } catch (e: UncategorizedMongoDbException){
                    delay(1000)
                }
            if (result==false) {
                kafkaPaymentRequestFailedProducer.send(
                    ProducerRecord("payment_request_failed", paymentRequestDTO.orderID)
                )
            } // if the result == true, debezium will send "payment_request_ok"
        }
    }

    @KafkaListener(
        topics=["abort_payment_request"],
        containerFactory = "paymentOrRefundRequestContainerFactory"
    )
    fun requestConsumer(abortRequestDTO: KafkaPaymentOrRefundRequestDTO){
        CoroutineScope(Dispatchers.IO).launch {
            var counter = 5
            var result : Boolean? = false
            while (counter-- > 0)
                try {
                    result = walletService.createRefundTransaction(abortRequestDTO)
                    break
                } catch (e: UncategorizedMongoDbException){
                    delay(1000)
                }
            if (result==false) {
                kafkaPaymentRequestFailedProducer.send(
                    ProducerRecord("abort_payment_request_failed", abortRequestDTO.orderID)
                )
            } // if the result == true, debezium will send "payment_request_ok"
        }
    }
}

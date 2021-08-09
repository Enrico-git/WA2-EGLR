package it.polito.wa2.walletservice.listeners

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

/**
 * This class is to check that debezium will send the correct response:
 * topic: "payment_request_ok"; payload: orderID
 * topic: "abort_payment_request_ok"; payload: orderID
 */
@Component
class MockDebeziumPaymentOrAbortRequestOk {
    @KafkaListener(
        topics=["payment_request_ok", "abort_payment_request_ok"],
        containerFactory = "mockPaymentRequestOkContainerFactory"
    )
    fun paymentRequestConsumer(mockPaymentOrAbortRequestOkDTO: String, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String){
        println("topic: $topic, payload: $mockPaymentOrAbortRequestOkDTO")
    }
}

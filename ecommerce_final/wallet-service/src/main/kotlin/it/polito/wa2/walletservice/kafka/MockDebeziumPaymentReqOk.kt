package it.polito.wa2.walletservice.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * This class is to check that debezium will send the correct response:
 * topic: "payment_request_ok"; payload: orderID
 */
@Component
class MockDebeziumPaymentReqOk {
    @KafkaListener(
        topics=["payment_request_ok"],
        containerFactory = "mockPaymentRequestOkContainerFactory"
    )
    fun paymentRequestConsumer(mockPaymentRequestOkDTO: String){
        println("mockPaymentRequestOkDTO: $mockPaymentRequestOkDTO")
    }
}

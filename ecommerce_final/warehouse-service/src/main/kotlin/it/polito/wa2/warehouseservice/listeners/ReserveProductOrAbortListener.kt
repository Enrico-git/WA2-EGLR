package it.polito.wa2.warehouseservice.listeners

import it.polito.wa2.warehouseservice.dto.AbortProductReservationRequestDTO
import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.warehouseservice.services.WarehouseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.data.mongodb.UncategorizedMongoDbException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class ReserveProductOrAbortListener(
        private val warehouseService: WarehouseService,
        private val kafkaReserveProductFailedProducer: KafkaProducer<String, String>
) {
    @KafkaListener(topics = ["reserve_products"],
    containerFactory = "reserveProductContainerFactory")
    fun reserveRequestConsumer(productsReservationRequestDTO: ProductsReservationRequestDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String){
        CoroutineScope(Dispatchers.IO).launch {
            println(productsReservationRequestDTO)
            var result: Boolean? = null
            try {
                result = warehouseService.reserveAllProducts(productsReservationRequestDTO)
            } catch (e: UncategorizedMongoDbException){
                delay(1000)
            }

            if(result==false){
                println("${topic}_failed")
                kafkaReserveProductFailedProducer.send(
                        ProducerRecord("${topic}_failed", productsReservationRequestDTO.orderID.toHexString())
                )
            } // in case of true result, Debezium will send "Reserve_Products_ok
        }
    }


    @KafkaListener(topics = ["abort_products_reservation"],
            containerFactory = "abortProductsReservationContainerFactory")
    fun abortRequestConsumer(abortProductReservationRequestDTO: AbortProductReservationRequestDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String){
        CoroutineScope(Dispatchers.IO).launch {
            var result: Boolean? = null
            try {
                result = warehouseService.abortReserveProduct(abortProductReservationRequestDTO)
            } catch (e: UncategorizedMongoDbException){
                delay(1000)
            }

            if(result==false){
                println("${topic}_failed")
                kafkaReserveProductFailedProducer.send(
                        ProducerRecord("${topic}_failed}", abortProductReservationRequestDTO.orderID.toHexString())
                )
            }
        }
    }
}
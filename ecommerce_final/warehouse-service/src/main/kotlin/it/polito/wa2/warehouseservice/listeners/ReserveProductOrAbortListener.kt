package it.polito.wa2.warehouseservice.listeners

import it.polito.wa2.warehouseservice.dto.ProductsReservationRequestDTO
import it.polito.wa2.warehouseservice.services.WarehouseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class ReserveProductOrAbortListener(
        private val warehouseService: WarehouseService
) {
//    @KafkaListener(topics = ["reserve_products"],
//    containerFactory = "reserveProductContainerFactory")
    fun reserveRequestConsumer(productsReservationRequestDTO: ProductsReservationRequestDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String){
        CoroutineScope(Dispatchers.IO).launch {
            val result = warehouseService.reserveProductOrAbort(topic, productsReservationRequestDTO)
        }
    }
//
//    @KafkaListener(topics = ["abort_products_reservation"],
//            containerFactory = "reserveProductContainerFactory")
//    fun abortRequestConsumer(productsReservationRequestDTO: ProductsReservationRequestDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String){
//        CoroutineScope(Dispatchers.IO).launch {
//            val result = warehouseService.reserveProductOrAbort(topic, productsReservationRequestDTO)
//        }
//    }
}
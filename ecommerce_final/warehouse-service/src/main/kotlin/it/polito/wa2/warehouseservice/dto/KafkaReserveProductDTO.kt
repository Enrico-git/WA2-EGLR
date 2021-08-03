package it.polito.wa2.warehouseservice.dto

data class KafkaReserveProductDTO(
        private val productId: String,
        private val quantity: String,
        private val warehouseId: String
)

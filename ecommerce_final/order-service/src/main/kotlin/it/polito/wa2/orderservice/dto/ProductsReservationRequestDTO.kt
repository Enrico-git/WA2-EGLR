package it.polito.wa2.orderservice.dto

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import it.polito.wa2.orderservice.dto.ProductDTO

data class ProductsReservationRequestDTO(
    val orderID: String,
    val products: Set<ProductDTO>
)

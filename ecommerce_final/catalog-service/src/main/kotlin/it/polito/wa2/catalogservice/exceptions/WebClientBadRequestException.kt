package it.polito.wa2.catalogservice.exceptions

import org.springframework.web.reactive.function.client.WebClientResponseException
import java.sql.Timestamp

data class WebClientBadRequestException (
    val timestamp: Timestamp,
    var status: Int = 400,
    var error: String,
) : WebClientResponseException(status, error, null, null, null, null) {
    override val message: String
        get() = error
}
package it.polito.wa2.catalogservice.common

/**
 * Enum class with the possible statuses of orders
 */
enum class OrderStatus {
    PENDING, ISSUED, DELIVERING, DELIVERED, FAILED, CANCELED
}
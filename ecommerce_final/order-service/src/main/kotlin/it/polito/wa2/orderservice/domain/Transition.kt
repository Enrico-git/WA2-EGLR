package it.polito.wa2.orderservice.domain

data class Transition(
    var source: String?,
    var target: String?,
    var event: String?,
    var action: (() -> Any?)?
)

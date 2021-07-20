package it.polito.wa2.orderservice.exceptions

class NotFoundException(message: String) : Exception(message)
class UnauthorizedException(message: String) : Exception(message)
class InvalidOperationException(message: String) : Exception(message)
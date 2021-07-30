package it.polito.wa2.catalogservice.exceptions

import graphql.ErrorClassification
import graphql.ErrorType

//class NotFoundException(message: String) : Exception(message)
class UnauthorizedException(message: String) : Exception(message)
//class InvalidOperationException(message: String) : Exception(message)

open class ValidationException(
    errorMessage: String? = "",
    private val parameters: Map<String, Any>? = mutableMapOf()
) : GraphQLException(errorMessage) {
    override val message: String?
        get() = super.message

    override fun getExtensions(): MutableMap<String, Any> {
        return mutableMapOf("parameters" to (parameters ?: mutableMapOf()))
    }

    override fun getErrorType(): ErrorClassification {
        return ErrorType.ValidationError
    }
}

open class IllegalArgumentException(
    errorMessage: String? = "",
    private val parameters: Map<String, Any>? = mutableMapOf()
) : GraphQLException(errorMessage) {
    override val message: String?
        get() = super.message

    override fun getExtensions(): MutableMap<String, Any> {
        return mutableMapOf("parameters" to (parameters ?: mutableMapOf()))
    }

    override fun getErrorType(): ErrorClassification {
        return ErrorType.InvalidSyntax
    }
}

open class NotFoundException(
    errorMessage: String? = "",
    private val parameters: Map<String, Any>? = mutableMapOf()
) : GraphQLException(errorMessage) {
    override val message: String?
        get() = super.message

    override fun getExtensions(): MutableMap<String, Any> {
        return mutableMapOf("parameters" to (parameters ?: mutableMapOf()))
    }

    override fun getErrorType(): ErrorClassification {
        return ErrorType.DataFetchingException
    }
}

open class InvalidOperationException(
    errorMessage: String? = "",
    private val parameters: Map<String, Any>? = mutableMapOf()
) : GraphQLException(errorMessage) {
    override val message: String?
        get() = super.message

    override fun getExtensions(): MutableMap<String, Any> {
        return mutableMapOf("parameters" to (parameters ?: mutableMapOf()))
    }

    override fun getErrorType(): ErrorClassification {
        return ErrorType.OperationNotSupported
    }
}

open class OptimisticLockingFailureException(
    errorMessage: String? = "",
    private val parameters: Map<String, Any>? = mutableMapOf()
) : GraphQLException(errorMessage) {
    override val message: String?
        get() = super.message

    override fun getExtensions(): MutableMap<String, Any> {
        return mutableMapOf("parameters" to (parameters ?: mutableMapOf()))
    }

    override fun getErrorType(): ErrorClassification {
        return ErrorType.ExecutionAborted
    }
}
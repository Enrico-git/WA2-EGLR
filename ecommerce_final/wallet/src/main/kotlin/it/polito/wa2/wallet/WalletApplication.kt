package it.polito.wa2.wallet

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import it.polito.wa2.wallet.exceptions.InvalidOperationException
import it.polito.wa2.wallet.exceptions.NotFoundException
import it.polito.wa2.wallet.exceptions.UnauthorizedException
import it.polito.wa2.wallet.routers.WalletHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.util.logging.Logger
import javax.validation.ValidationException


/**
 * Note: all APIs the wallet offer are internal one (Catalog only could invoke them)
 *
 * GET  /{walletID} -> retrieve the wallet information given a target ID.
 * POST / -> create a new wallet for the related user (passed in JWT).
 * POST /{walletID}/transactions -> Adds a new transaction to the wallet identified by walletID.
 *                                  Please notice that positive transaction could be added by admin only.
 * GET  /{walletID}/transactions?from=<dateInMillis>&to=<dateInMillis> -> Retrieves a list of transactions
 *                                                                        regarding a given wallet in a given
 *                                                                        time frame
 * GET  /{walletID}/transactions/{transactionID} -> Retrieves the details of a single transaction.
 */

@SpringBootApplication
@EnableEurekaClient
class WalletApplication{

    @Bean
    fun getLogger(): Logger = Logger.getLogger("it.polito.wa2.walletServiceLogger")

    /**
     * router is similar to a controller but it reduces bootstrap time since less annotation are needed.
     * coRouter ('co' since we are in reactive kotlin)
     * Here more on RouterFunctionDsl:
     * https://docs.spring.io/spring-framework/docs/current/kdoc-api/spring-framework/org.springframework.web.reactive.function.server/-router-function-dsl/index.html
     * see Functional endpoints (1.5 paragraph) from here:
     * https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html
     */

    @Bean
    fun walletRoutes(walletHandler: WalletHandler) = coRouter {
        /**
         * 'nest' is used to group all API with the same prefix (/wallets):
         * walletHandler contains the handler functions needed for solving requests.
         */
        "/wallets".nest {
            accept(MediaType.APPLICATION_NDJSON).nest {
                GET("/{walletID}", walletHandler::getWallet)
                POST("/", walletHandler::createWallet)
                POST("/{walletID}/transactions", walletHandler::createTransaction)
                GET("/{walletID}/transactions", walletHandler::getWalletTransactions)
                GET("/{walletID}/transactions/{transactionID}", walletHandler::getWalletTransaction)
            }
        }
        /**
         * 	Declaring the 'before' and 'after' filters at the bottom,
         * 	the logs of request/response are applied to all routes.
         */
        before {
            getLogger().info("Doing : $it")
            ServerRequest.from(it).build()
        }
        after { serverRequest, serverResponse ->
            getLogger().info("Ended: $serverRequest with ${serverResponse.statusCode()}")
            serverResponse
        }

        /**
         * The onError is applied to all previous route also.
         * It's similar to @ExceptionHandler and @ControllerAdvice
         */
        onError<NotFoundException> {e, _ ->  status(HttpStatus.NOT_FOUND).bodyValueAndAwait(e.localizedMessage)}
        onError<ValidationException> {e, _ ->  status(HttpStatus.UNPROCESSABLE_ENTITY).bodyValueAndAwait(e.localizedMessage)}
        onError<IllegalArgumentException> {e, _ ->  status(HttpStatus.BAD_REQUEST).bodyValueAndAwait(e.localizedMessage)}
        onError<OptimisticLockingFailureException> {e, _ ->  status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValueAndAwait(e.localizedMessage)}
        onError<InvalidOperationException> {e, _ ->  status(HttpStatus.CONFLICT).bodyValueAndAwait(e.localizedMessage)}
        onError<UnauthorizedException> { e, _ ->  status(HttpStatus.UNAUTHORIZED).bodyValueAndAwait(e.localizedMessage)}
        onError<Exception> {e, _ ->  status(HttpStatus.BAD_REQUEST).bodyValueAndAwait(e.localizedMessage)}
    }

    /**
     * MongoDB reactive configuration
     */
    @EnableReactiveMongoRepositories
    class MongoReactiveConfig : AbstractReactiveMongoConfiguration() {
        @Bean
        fun mongoClient(): MongoClient {
            return MongoClients.create()
        }

        override fun getDatabaseName() = "walletservice"

//        @Bean
//        override fun reactiveMongoTemplate()
//                = ReactiveMongoTemplate(mongoClient(), 'walletservice')
    }


}

fun main(args: Array<String>) {
    runApplication<WalletApplication>(*args)
}

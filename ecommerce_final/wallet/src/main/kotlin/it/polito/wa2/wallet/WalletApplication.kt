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

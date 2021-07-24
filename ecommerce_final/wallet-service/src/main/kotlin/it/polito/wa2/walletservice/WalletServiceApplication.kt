package it.polito.wa2.walletservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import java.util.logging.Logger


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
class WalletServiceApplication{

    @Bean
    fun getLogger(): Logger = Logger.getLogger("it.polito.wa2.walletServiceLogger")
}

fun main(args: Array<String>) {
    runApplication<WalletServiceApplication>(*args)
}

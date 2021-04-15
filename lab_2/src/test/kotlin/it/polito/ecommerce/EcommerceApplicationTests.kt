package it.polito.ecommerce


import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.rybalkinsd.kohttp.dsl.httpGet
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.ext.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import it.polito.ecommerce.dto.ErrorDTO
import it.polito.ecommerce.dto.TransactionDTO
import it.polito.ecommerce.dto.WalletDTO
import jdk.internal.org.objectweb.asm.TypeReference
import okhttp3.Response
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal


@SpringBootTest
class EcommerceApplicationTests {

    /*
        /wallet/{walletId} [GET] à Get the details of a wallet. The response body will be the requested
        wallet, and the response status 200 (ok)
    */

    @Test
    fun getWalletExistingID(){
        val walletID: Long = 10
        val response: Response = "http://localhost:8080/wallet/${walletID}".httpGet()

        val walletDTO = jacksonObjectMapper().readValue(response.body()?.string(), WalletDTO::class.java)
        println(walletDTO)
        assertTrue(response.code()==200 && walletDTO.id == walletID)
    }

    @Test
    fun getWalletNegativeID(){
        val walletID: Long = -1
        val response: Response = "http://localhost:8080/wallet/${walletID}".httpGet()

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        assertTrue(errorDTO.status == 422)
    }

    @Test
    fun getWalletNotExistingID(){
        val walletID: Long = 10000
        val response: Response = "http://localhost:8080/wallet/${walletID}".httpGet()

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        assertTrue(errorDTO.status == 404)
    }


    /*
        /wallet/{walletId}/transaction [POST] à Create a transaction taking the amount of money set in the
        body from the given wallet and transferring it to a second walletId, always defined in the body.
        Return the created transaction
     */

    @Test
    fun postWalletExistingCustomerID(){
        val walletID: Long = 2
        val response: Response = httpPost {
            url("http://localhost:8080/wallet/")

            body {
                json {
                    "id" to "$walletID" // customerwalletID
                }
            }
        }

        val walletDTO = jacksonObjectMapper().readValue(response.body()?.string(), WalletDTO::class.java)
        println(response.code())
        println(walletDTO)
        assertTrue(response.code()==201 && walletDTO.customer.id == walletID)
    }

    @Test
    fun postWalletNegativeCustomerID(){
        val walletID: Long = -1
        val response: Response = httpPost {
            url("http://localhost:8080/wallet/")

            body {
                json {
                    "id" to "$walletID" // customerID
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        assertTrue(errorDTO.status == 422)
    }

    @Test
    fun postWalletNotExistingCustomerID(){
        val walletID: Long = 10000
        val response: Response = httpPost {
            url("http://localhost:8080/wallet/")

            body {
                json {
                    "id" to "$walletID" // customerID
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        assertTrue(errorDTO.status == 400)
    }

    /*
        /wallet/{walletId}/transaction [POST] à Create a transaction taking the amount of money set in the
        body from the given wallet and transferring it to a second walletId, always defined in the body.
        Return the created transaction
     */

    @Test
    fun postSenderWalletIDTransaction(){
        val senderWalletID: Long = 10
        val receiverWalletID: Long = 12
        val amount = BigDecimal(1024.48)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val transactionDTO = jacksonObjectMapper().readValue(response.body()?.string(), TransactionDTO::class.java)
        println(transactionDTO)
        println(response.code())
        /*
        TransactionDTO(
            id=20, senderID=10, receiverID=12,
            sender=Jon Doe, receiver=Alice InWonderland,
            timestamp=2021-04-08 21:41:05.64,
            amount=1024.48000000000001818989403545856475830078125
        )
        201
         */
        assertTrue(
            response.code() == 201 &&
            transactionDTO.senderID==senderWalletID &&
            transactionDTO.receiverID==receiverWalletID &&
            transactionDTO.amount==amount
        )
    }

    @Test
    fun postNegativeSenderWalletIDTransaction(){
        val senderWalletID: Long = -2
        val receiverWalletID: Long = 2
        val amount = BigDecimal(32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        assertTrue(errorDTO.status == 422)
    }

    @Test
    fun postNegativeReceiverWalletIDTransaction(){
        val senderWalletID: Long = 2
        val receiverWalletID: Long = -2
        val amount = BigDecimal(32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        assertTrue(errorDTO.status == 422)
    }

    @Test
    fun postNullReceiverWalletIDTransaction(){
        val senderWalletID: Long = 2
        val receiverWalletID: Long? = null
        val amount = BigDecimal(32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        /*
        {
          "timestamp": "2021-04-08T19:21:20.084+00:00",
          "status": 400,
          "error": "Sender or receiver Wallet not Exists"
        }
         */
        assertTrue(errorDTO.status == 400)
    }

    @Test
    fun postNotExistingReceiverWalletIDTransaction(){
        val senderWalletID: Long = 10
        val receiverWalletID: Long = 10000
        val amount = BigDecimal(32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        /*
        {
          "timestamp": "2021-04-08T19:21:20.084+00:00",
          "status": 400,
          "error": "Sender or receiver Wallet not Exists"
        }
         */
        assertTrue(errorDTO.status == 400)
    }

    @Test
    fun postNotExistingSenderWalletIDTransaction(){
        val senderWalletID: Long = 10000
        val receiverWalletID: Long = 11
        val amount = BigDecimal(32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        /*
        {
          "timestamp": "2021-04-08T19:21:20.084+00:00",
          "status": 400,
          "error": "Sender or receiver Wallet not Exists"
        }
         */
        assertTrue(errorDTO.status == 400)
    }

    @Test
    fun postSameWalletIDTransaction(){
        val senderWalletID: Long = 11
        val receiverWalletID: Long = 11
        val amount = BigDecimal(32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        /*
        {
          "timestamp": "2021-04-08T19:28:23.448+00:00",
          "status": 400,
          "error": "You can't send money to yourself"
        }
         */
        assertTrue(errorDTO.status == 400)
    }

    @Test
    fun postSenderWalletIDNotEnoughTransaction(){
        val senderWalletID: Long = 11
        val receiverWalletID: Long = 12
        val amount = BigDecimal(32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        /*
        {
          "timestamp": "2021-04-08T19:28:23.448+00:00",
          "status": 400,
          "error": "Balance not enough"
        }
         */
        assertTrue(errorDTO.status == 400)
    }

    @Test
    fun postSenderWalletIDNegativeAmountTransaction(){
        val senderWalletID: Long = 11
        val receiverWalletID: Long = 12
        val amount = BigDecimal(-32.15)

        val response: Response = httpPost {
            url("http://localhost:8080/wallet/$senderWalletID/transaction")

            body {
                json {
                    "receiverID" to receiverWalletID // customerID
                    "amount" to amount
                }
            }
        }

        val errorDTO = jacksonObjectMapper().readValue(response.body()?.string(), ErrorDTO::class.java)
        println(errorDTO)
        /*
        {
          "timestamp": "2021-04-08T19:28:23.448+00:00",
          "status": 422,
          "error": "amount - the transaction must be higher than zero"
        }
         */
        assertTrue(errorDTO.status == 422)
    }

    /*
        /wallet/{walletId}/transactions?from=<dateInMillis>&to=<dateInMillis> [GET] à Get a list of
        transactions regarding a given wallet in a given time frame
     */

    @Test
    fun getWalletTransactions(){
        val walletID: Long = 10
        val from: Long? = null
        val to: Long? = null
        val response: Response = httpGet {
            url("http://localhost:8080/wallet/$walletID/transactions")
            param {
                "from" to from
                "to" to to
            }
        }

        //TODO
        //val transactionsDTO = jacksonObjectMapper().readValue(response.body()?.string(),
        //    object : TypeReference<List<TransactionDTO::class.java>>(){} )

//        println(transactionsDTO)
        println(response.code())

        assertTrue(response.code() == 200 )
    }


}

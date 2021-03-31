package it.polito.ecommerce.dto


data class WalletDTO(
    val id: Int,
    val balance: Double,
    val customer: Int
//    val transactionsSent: List<TransactionDTO>,
//    val transactionsRecv: List<TransactionDTO>
)
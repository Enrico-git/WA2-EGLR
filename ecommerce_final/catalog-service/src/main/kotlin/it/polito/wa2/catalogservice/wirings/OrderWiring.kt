package it.polito.wa2.catalogservice.wirings

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.TypeRuntimeWiring
import it.polito.wa2.catalogservice.dto.*
import it.polito.wa2.catalogservice.queries.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.springframework.graphql.boot.RuntimeWiringBuilderCustomizer
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Component
class OrderWiring(
    private val orderQuery: OrderQuery,
    private val walletQuery: WalletQuery,
    private val warehouseQuery: WarehouseQuery,
    private val catalogMutation: CatalogMutation,
    private val warehouseMutation: WarehouseMutation
    ): RuntimeWiringBuilderCustomizer {

    override fun customize(builder: RuntimeWiring.Builder) {
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("orders",
                DataFetcher<Flux<OrderDTO>>{ env: DataFetchingEnvironment? ->
                    orderQuery.orders()

                })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("order",
                DataFetcher<Mono<OrderDTO>> { env: DataFetchingEnvironment? -> orderQuery.order(env!!.getArgument("orderID"),env!!.getArgument("token")) })
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("newOrder") { env ->
                catalogMutation.newOrder(
                    env.getArgument("buyerID"),
                    env.getArgument("products"),
                    env.getArgument("delivery"),
                    env.getArgument("email"),
                    env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("deleteOrder") { env ->
                catalogMutation.deleteOrder(env.getArgument("orderID"),env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("updateOrder") { env ->
                catalogMutation.updateOrder(
                    env.getArgument("orderID"),
                    env.getArgument("buyerID"),
                    env.getArgument("products"),
                    env.getArgument("delivery"),
                    env.getArgument("email"),
                    env.getArgument("token")) }
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("wallet",
                DataFetcher<Mono<WalletDTO>> { env: DataFetchingEnvironment? -> walletQuery.wallet(env!!.getArgument("walletID"),env!!.getArgument("token")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("transactions",
                DataFetcher<Flux<TransactionDTO>> { env: DataFetchingEnvironment? -> walletQuery.transactions(
                    env!!.getArgument("walletID"),
                    env.getArgument("from"),
                    env.getArgument("to"),
                    env.getArgument("page"),
                    env.getArgument("size"),
                    env.getArgument("token")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("transaction",
                DataFetcher<Mono<TransactionDTO>> { env: DataFetchingEnvironment? -> walletQuery.transaction(
                    env!!.getArgument("walletID"),
                    env.getArgument("transactionID"),
                    env.getArgument("token")) })
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("newWallet") { env ->
                catalogMutation.newWallet(
                    env.getArgument("customerID"),
                    env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("newTransaction") { env ->
                catalogMutation.newTransaction(
                    env.getArgument("walletID"),
                    env.getArgument("amount"),
                    env.getArgument("description"),
                    env.getArgument("orderID"),
                    env.getArgument("token")) }
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("warehouses",
                DataFetcher<Flux<WarehouseDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.warehouses(env!!.getArgument("token")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("warehouse",
                DataFetcher<Mono<WarehouseDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.warehouse(env!!.getArgument("warehouseID"),env.getArgument("token")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("products",
                DataFetcher<Flux<ProductDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.products(env!!.getArgument("category")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("product",
                DataFetcher<Mono<ProductDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.product(env!!.getArgument("productID")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("picture",
                DataFetcher<Mono<String>> { env: DataFetchingEnvironment? -> warehouseQuery.picture(env!!.getArgument("productID")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("productWarehouses",
                DataFetcher<Flux<WarehouseDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.productWarehouses(env!!.getArgument("productID")) })
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("deleteProduct") { env ->
                warehouseMutation.deleteProduct(
                    env.getArgument("productID"),
                    env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("updatePicture") { env ->
                warehouseMutation.updatePicture(
                    env.getArgument("productID"),
                    env.getArgument("picture"),
                    env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
                wiring.dataFetcher("deleteWarehouse") { env ->
                warehouseMutation.deleteWarehouse(
                    env.getArgument("warehouseID"),
                    env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("newWarehouse") { env ->
                warehouseMutation.newWarehouse(
                    env.getArgument("products"),
                    env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("patchWarehouse") { env ->
                warehouseMutation.patchWarehouse(
                    env.getArgument("warehouseID"),
                    env.getArgument("products"),
                    env.getArgument("token")) }
        }
        builder.type(
            "MutationType"
        ) { wiring ->
            wiring.dataFetcher("updateWarehouse") { env ->
                warehouseMutation.updateWarehouse(
                    env.getArgument("warehouseID"),
                    env.getArgument("products"),
                    env.getArgument("token")) }
        }
    }
}


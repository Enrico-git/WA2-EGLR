package it.polito.wa2.catalogservice.wirings

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.TypeRuntimeWiring
import it.polito.wa2.catalogservice.queries.OrderQuery
import it.polito.wa2.catalogservice.queries.WalletQuery
import it.polito.wa2.catalogservice.queries.WarehouseQuery
import it.polito.wa2.catalogservice.dto.*
import it.polito.wa2.catalogservice.queries.CatalogMutation
import kotlinx.coroutines.flow.Flow
import org.springframework.graphql.boot.RuntimeWiringBuilderCustomizer
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class OrderWiring(
    private val orderQuery: OrderQuery,
    private val walletQuery: WalletQuery,
    private val warehouseQuery: WarehouseQuery,
    private val catalogMutation: CatalogMutation
    ): RuntimeWiringBuilderCustomizer {

    override fun customize(builder: RuntimeWiring.Builder) {
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("orders",
                DataFetcher<Flow<OrderDTO>> { env: DataFetchingEnvironment? -> orderQuery.orders(env!!.getArgument("token")) })
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
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("wallet",
                DataFetcher<Mono<WalletDTO>> { env: DataFetchingEnvironment? -> walletQuery.wallet(env!!.getArgument("walletID"),env!!.getArgument("token")) })
        }
        builder.type(
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("transactions",
                DataFetcher<Flow<TransactionDTO>> { env: DataFetchingEnvironment? -> walletQuery.transactions(
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
            "QueryType"
        ) { wiring: TypeRuntimeWiring.Builder ->
            wiring.dataFetcher("warehouses",
                DataFetcher<Flow<WarehouseDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.warehouses(env!!.getArgument("token")) })
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
                DataFetcher<Flow<ProductDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.products(env!!.getArgument("category")) })
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
                DataFetcher<Flow<WarehouseDTO>> { env: DataFetchingEnvironment? -> warehouseQuery.productWarehouses(env!!.getArgument("productID")) })
        }

    }
}
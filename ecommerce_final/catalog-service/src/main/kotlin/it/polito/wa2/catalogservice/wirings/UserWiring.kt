package it.polito.wa2.catalogservice.wirings

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.TypeRuntimeWiring
import it.polito.wa2.catalogservice.dto.LoginDTO
import it.polito.wa2.catalogservice.services.UserDetailsService
import org.springframework.graphql.boot.RuntimeWiringBuilderCustomizer
import org.springframework.stereotype.Component


@Component
class UserWiring(private val service: UserDetailsService) : RuntimeWiringBuilderCustomizer {

    override fun customize(wiringBuilder: RuntimeWiring.Builder) {
        wiringBuilder.type(
            "QueryType"
        ) { builder: TypeRuntimeWiring.Builder ->
            builder.dataFetcher("signIn",
                DataFetcher<LoginDTO> { env: DataFetchingEnvironment? -> service.authAndCreateToken(env!!.getArgument("loginDTO")) })
        }
        /*wiringBuilder.type(
            "QueryType"
        ) { builder: TypeRuntimeWiring.Builder ->
            builder.dataFetcher("registrationConfirm",
                DataFetcher<Unit> { env: DataFetchingEnvironment? -> service.verifyToken(env!!.getArgument("token")) })
        }*/
    }

}
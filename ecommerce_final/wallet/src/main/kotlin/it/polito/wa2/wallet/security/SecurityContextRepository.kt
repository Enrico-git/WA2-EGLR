package it.polito.wa2.wallet.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.logging.Logger

/**
 * Handles the authentication procedure between HTTP req/resp
 */
@Component
class SecurityContextRepository(
    private val authenticationManager: AuthenticationManager,
    private val logger: Logger
) : ServerSecurityContextRepository {

    override fun save(swe: ServerWebExchange?, sc: SecurityContext?): Mono<Void>? {
        throw UnsupportedOperationException("Not supported yet.")
    }

    /**
     * Strips the JWT token from the Authorization header and adds in to the authentication
     * @param swe Contract that provides access to req/resp
     * @return Mono of the security context
     */
    override fun load(swe: ServerWebExchange): Mono<SecurityContext>? {
        logger.info("Making Security Context (read Authentication header)")
        return Mono.justOrEmpty(swe.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter{it.startsWith("Bearer ")}
            .flatMap{ token ->
                val authToken = token.substring(7)
                val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
                this.authenticationManager
                    .authenticate(auth) //verify signature and return null or UserDetailsDTO
                    .map{SecurityContextImpl(it)}
            }
    }
}

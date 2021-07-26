package it.polito.wa2.walletservice.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * Class that overrides the default authentication manager in a reactive way
 */
@Component
class AuthenticationManager(
    private val jwtUtils: JwtUtils
) : ReactiveAuthenticationManager {
    /**
     * Checks if the JWT signature is valid and
     * @param authentication token for authentication request
     * @return mono of authenticated user if signature is valid, else null
     */
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        val jwtToken = authentication?.credentials.toString()
        return Mono
            .just(jwtUtils.validateJwtToken(jwtToken)) //we can avoid it if we decide to not check signature
            .filter{ it }
            .switchIfEmpty(Mono.empty())
            .map{
                val userDetailsDTO = jwtUtils.getDetailsFromJwtToken(jwtToken)
                UsernamePasswordAuthenticationToken(
                    userDetailsDTO,
                    jwtToken,
                    userDetailsDTO.authorities //redundant since userDetailsDTO already contains roles
                )
            }
    }
}

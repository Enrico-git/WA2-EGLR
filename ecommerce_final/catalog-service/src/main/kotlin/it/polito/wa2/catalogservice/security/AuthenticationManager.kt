package it.polito.wa2.catalogservice.security

import io.jsonwebtoken.Claims
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors


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
        return Mono.just(jwtUtils.validateJwtToken(jwtToken))
            .filter{ it }
            .switchIfEmpty(Mono.empty())
            .map{
                val userDetailsDTO = jwtUtils.getDetailsFromJwtToken(jwtToken)
                UsernamePasswordAuthenticationToken(
                    userDetailsDTO,
                    jwtToken,
                    userDetailsDTO.authorities
                )
            }
    }
}
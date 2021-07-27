package it.polito.wa2.warehouseservice.security

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AuthenticationManager (
        private val jwtUtils: JwtUtils
): ReactiveAuthenticationManager{

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
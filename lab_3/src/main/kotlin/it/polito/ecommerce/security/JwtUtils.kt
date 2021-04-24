package it.polito.ecommerce.security

import io.jsonwebtoken.*
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import io.jsonwebtoken.security.Keys
import it.polito.ecommerce.dto.UserDetailsDTO
import org.springframework.beans.factory.annotation.Value
import java.security.Key
import java.util.*

import java.security.SignatureException


@Component
class JwtUtils {
    @Value("\${application.jwt.jwtSecret}")
    private val jwtSecret: String = ""

    @Value("\${application.jwt.jwtExpirationMs}")
    private val jwtExpirationMs = 0

    val secretKey: Key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateJwtToken(authentication: Authentication): String{
        val userPrincipal: UserDetailsDTO = authentication.principal as UserDetailsDTO

        val claim: Claims = Jwts.claims(mapOf(Pair("roles", userPrincipal.roles.toString())))

        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setClaims(claim)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parse(authToken)
            return true
        } catch (e: SignatureException) {
            println("Invalid JWT signature: ${e.message}")
        } catch (e: MalformedJwtException) {
            println("Invalid JWT token: ${e.message}")
        } catch (e: ExpiredJwtException) {
            println("JWT token is expired: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            println("JWT token is unsupported: ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("JWT claims string is empty: ${e.message}")
        }
        return false
    }

    fun getDetailsFromJwtToken(authToken: String): UserDetailsDTO{
        val parsedToken = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken).body
        return UserDetailsDTO(
            id = null,
            username = parsedToken.subject,
            password = null,
            roles = parsedToken["roles"].toString(),
            isEnabled = null,
            email = null
        )
    }
}
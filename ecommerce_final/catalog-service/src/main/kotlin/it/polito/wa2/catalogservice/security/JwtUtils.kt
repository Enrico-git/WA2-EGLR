package it.polito.wa2.catalogservice.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import it.polito.wa2.catalogservice.dto.UserDetailsDTO
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import javax.annotation.PostConstruct

@Component
class JwtUtils {

    @Value("\${application.jwt.jwtSecret}")
    private val jwtSecret: String? = null

    @Value("\${application.jwt.jwtExpirationMs}")
    private val jwtExpirationMs = 0

    private lateinit var secretKey: Key

    @PostConstruct
    fun post() {
        secretKey = Keys.hmacShaKeyFor(jwtSecret!!.toByteArray())
    }

    fun generateJwtToken(authentication: Authentication): String {

        val userPrincipal: UserDetailsDTO = authentication.principal as UserDetailsDTO
        val claims: Claims = Jwts.claims(mapOf(Pair("roles", userPrincipal.roles), Pair("sub", userPrincipal.username)))
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateJwtToken(authToken: String): Boolean {

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parse(authToken)
            return true
        } catch (e: io.jsonwebtoken.security.SignatureException) {
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

    fun getDetailsFromJwtToken(authToken: String): UserDetailsDTO {
        val parsedToken = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(authToken).body
//        println(parsedToken)
        return UserDetailsDTO(
            id = ObjectId(parsedToken["sub"].toString()),
            username = null,
            roles = parsedToken["roles"].toString(),
            isEnabled = null,
            password = null,
            email = null
        )
    }
}
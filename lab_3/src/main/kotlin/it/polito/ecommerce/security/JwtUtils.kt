package it.polito.ecommerce.security

import io.jsonwebtoken.*
import it.polito.ecommerce.dto.UserDetailsDTO
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import java.security.Key
import java.util.*

import java.security.SignatureException
import javax.annotation.PostConstruct


@Component
class JwtUtils {


    @Value("\${application.jwt.jwtSecret}")
    private val jwtSecret: String? = null

    @Value("\${application.jwt.jwtExpirationMs}")
    private val jwtExpirationMs = 0

    //val secretKey: Key = Keys.hmacShaKeyFor("that_depends_a_good_deal_on_where_you_want_to_get_to_then_it_doesnt_much_matter_which_way_you_go".toByteArray())

    private lateinit var secretKey: Key

    @PostConstruct
    fun post() {
        secretKey = Keys.hmacShaKeyFor(jwtSecret!!.toByteArray())
    }

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal: UserDetailsDTO = authentication.principal as UserDetailsDTO
        val claims: Claims = Jwts.claims(mapOf(Pair("roles", userPrincipal.roles), Pair("sub", userPrincipal.username)))
        //val claim: Claims = Jwts.claims(mapOf(Pair("roles", userPrincipal.roles.toString())))
        return Jwts.builder()
            //.setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .setClaims(claims)
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()

    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parse(authToken)
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
        val parsedToken = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(authToken).body
        return UserDetailsDTO(
            id = null,
            username = parsedToken["sub"].toString(),
            roles = parsedToken["roles"].toString(),
            password = null,
            email = null,
            isEnabled = null
        )
    }

}
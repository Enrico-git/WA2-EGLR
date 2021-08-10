package it.polito.wa2.warehouseservice.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import it.polito.wa2.warehouseservice.dto.UserDetailsDTO
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Key
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

    fun validateJwtToken(authToken: String): Boolean{

        try{
            Jwts.parserBuilder().setSigningKey(secretKey).build().parse(authToken)
            return true
        }catch(e: io.jsonwebtoken.security.SecurityException){
            println("Invalid JWT signature: ${e.message}")
        }catch (e: MalformedJwtException) {
            println("Invalid JWT token: ${e.message}")
        }catch(e: ExpiredJwtException){
            println("JWT token is expired: ${e.message}")
        }catch(e: UnsupportedJwtException){
            println("JWT token is unsopported: ${e.message}")
        }catch(e: IllegalArgumentException){
            println("JWT claims string is empty: ${e.message}")
        }

        return false
    }

    fun getDetailsFromJwtToken(authToken: String): UserDetailsDTO{
        val parsedToken = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(authToken)
                .body
        val user = UserDetailsDTO(
                id = ObjectId(parsedToken["sub"].toString()),
                username = null,
                roles = parsedToken["roles"].toString(),
                isEnabled = null,
                password = null,
                email = null
        )
        return user
    }

}
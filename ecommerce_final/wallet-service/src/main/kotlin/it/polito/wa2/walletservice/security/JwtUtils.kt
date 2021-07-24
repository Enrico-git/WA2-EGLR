package it.polito.wa2.walletservice.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import it.polito.wa2.walletservice.dto.UserDetailsDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Component
class JwtUtils(
    private val logger: Logger
) {

    @Value("\${application.jwt.jwtSecret}")
    private val jwtSecret: String? = null

    @Value("\${application.jwt.jwtExpirationMs}")
    private val jwtExpirationMs = 0

    private lateinit var secretKey: Key

    @PostConstruct
    fun post() {
        secretKey = Keys.hmacShaKeyFor(jwtSecret!!.toByteArray())
    }

    fun validateJwtToken(authToken: String): Boolean {
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parse(authToken)
            logger.info("JWT is valid...")
            return true
        } catch (e: io.jsonwebtoken.security.SignatureException) {
            logger.info("Invalid JWT signature: ${e.message}")
        } catch (e: MalformedJwtException) {
            logger.info("Invalid JWT token: ${e.message}")
        } catch (e: ExpiredJwtException) {
            logger.info("JWT token is expired: ${e.message}")
        } catch (e: UnsupportedJwtException) {
            logger.info("JWT token is unsupported: ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.info("JWT claims string is empty: ${e.message}")
        }
        return false
    }

    fun getDetailsFromJwtToken(authToken: String): UserDetailsDTO {
        val parsedToken = Jwts
            .parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(authToken)
            .body
//        logger.info(parsedToken)
        return UserDetailsDTO(
            id = null,
            username = parsedToken["sub"].toString(),
            roles = parsedToken["roles"].toString(),
            isEnabled = null,
            password = null,
            email = null
        )
    }
}

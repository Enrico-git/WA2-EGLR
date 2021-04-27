package it.polito.ecommerce.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationTokenFilter(
    private val jwtUtils: JwtUtils
): OncePerRequestFilter() {


    fun parseJwt(request: HttpServletRequest): String?{
        val headerAuth = request.getHeader("Authorization")
        return if(headerAuth.isNotBlank() && headerAuth.startsWith("Bearer ")) {
            headerAuth.substring(7, headerAuth.length)
        } else
            null
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwtToken = parseJwt(request)

            println(jwtToken)
            if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
                val userDetailsDTO = jwtUtils.getDetailsFromJwtToken(jwtToken)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetailsDTO,
                    null,
                    userDetailsDTO.authorities
                )

                authentication.details = WebAuthenticationDetailsSource()
                    .buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }catch (e: Exception){
            println("Cannot set user authentication: $e")
        }

        filterChain.doFilter(request, response)
    }

}
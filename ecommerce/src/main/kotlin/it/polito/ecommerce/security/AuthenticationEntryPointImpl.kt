package it.polito.ecommerce.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthenticationEntryPointImpl : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response!!.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.reasonPhrase)
    }
}
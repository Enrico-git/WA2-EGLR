package it.polito.ecommerce.security

import it.polito.ecommerce.services.UserDetailsServiceExt
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint

@Configuration
class WebSecurityConfig(
    val passwordEncoder: PasswordEncoder,
    val userDetailsServiceExt: UserDetailsServiceExt,
    val authenticationEntryPoint: AuthenticationEntryPoint
): WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/auth/**")
            .permitAll()
        .and()
            .formLogin()
            .loginProcessingUrl("/auth/login")
        .and()
            .logout()
            .logoutUrl("/auth/logout")

        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
    }
}
package it.polito.ecommerce.security

import it.polito.ecommerce.services.UserDetailsServiceExt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsServiceExt: UserDetailsServiceExt,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val jwtAuthenticationTokenFilter: JwtAuthenticationTokenFilter
): WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/auth/**")
            .permitAll()
        .and()
            .authorizeRequests()
            .antMatchers("/wallet/**")
            .hasAuthority("CUSTOMER")

        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)

        http.addFilterBefore(jwtAuthenticationTokenFilter,
            UsernamePasswordAuthenticationFilter::class.java)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(userDetailsServiceExt)
            .passwordEncoder(passwordEncoder)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }
}
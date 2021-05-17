package it.polito.ecommerce.security

import it.polito.ecommerce.services.UserDetailsServiceExt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.authentication.AuthenticationManager

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsServiceExt: UserDetailsServiceExt,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val jwtAuthenticationTokenFilter: JwtAuthenticationTokenFilter
): WebSecurityConfigurerAdapter() {

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(userDetailsServiceExt)
            .passwordEncoder(passwordEncoder)

    }

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/auth/**")
            .permitAll()
        .and()
            .authorizeRequests()
            .antMatchers("/user/**")
            .hasAuthority("CUSTOMER")
        .and()
            .authorizeRequests()
            .antMatchers("/wallet/**")
            .hasAuthority("CUSTOMER")
        .and()
            .csrf()
            .disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
        .and()
            .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

}

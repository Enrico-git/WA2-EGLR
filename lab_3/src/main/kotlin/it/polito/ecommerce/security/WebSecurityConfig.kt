package it.polito.ecommerce.security

import it.polito.ecommerce.repositories.WalletRepository
import it.polito.ecommerce.services.UserDetailsServiceExt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration
import java.lang.Exception
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler





@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@Profile("test")
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

    //access user service in injection from SecurityConf constructor
    override fun configure(auth: AuthenticationManagerBuilder) {
//        super.configure(auth)
        //define simple users without setting up DB - demo only
        //not support application.property anymore
        auth
            .userDetailsService(userDetailsServiceExt)
            .passwordEncoder(passwordEncoder)

    }

    //define which URL are protected and which not
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
            .addFilterBefore(jwtAuthenticationTokenFilter,
            UsernamePasswordAuthenticationFilter::class.java)

//        http.cors().disable()
        //http.cors() //only javascript can make request because coming from us
    }


}

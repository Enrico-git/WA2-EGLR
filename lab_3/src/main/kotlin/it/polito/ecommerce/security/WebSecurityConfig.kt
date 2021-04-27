package it.polito.ecommerce.security

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
import java.lang.Exception


@Configuration
@EnableWebSecurity
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
//        super.configure(http)
        //tutti quelli che iniziano con secure devono essere autenticati
        //.antMatchers("/secure/**")
        http
            .authorizeRequests()
            .antMatchers("/auth/**")
            .permitAll()
            .and()
            .authorizeRequests()
            .antMatchers("/wallet/**")
            .hasAuthority("CUSTOMER")
//            .hasRole("CUSTOMER")
//        .and()
//            .formLogin()
//            .loginPage("/auth/signin")
//            .loginProcessingUrl("/auth/login")
//        .and()
//            .logout()
//            .logoutUrl("/auth/logout")


        http.csrf().disable() //value not sent in logout form
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)

        http.addFilterBefore(jwtAuthenticationTokenFilter,
            UsernamePasswordAuthenticationFilter::class.java)
//        http.cors().disable()
        //http.cors() //only javascript can make request because coming from us
    }
}
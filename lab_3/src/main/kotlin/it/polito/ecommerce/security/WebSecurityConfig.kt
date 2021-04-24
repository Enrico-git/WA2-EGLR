package it.polito.ecommerce.security

import it.polito.ecommerce.services.UserDetailsServiceExt
import it.polito.ecommerce.services.UserDetailsServiceExtImpl
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint

@Configuration
class WebSecurityConfig(
    private val passwordEncoder: PasswordEncoder,
    private val userDetailsServiceExt: UserDetailsServiceExt,
    private val authenticationEntryPoint: AuthenticationEntryPoint
): WebSecurityConfigurerAdapter() {

    //access user service in injection from SecurityConf constructor
    override fun configure(auth: AuthenticationManagerBuilder) {
        //super.configure(auth)
        //define simple users without setting up DB - demo only
        //not support application.property anymore
        println("p1 is econded to ${passwordEncoder.encode("p1")}")
        println("p2 is econded to ${passwordEncoder.encode("p2")}")


        auth.inMemoryAuthentication()
            .withUser("u1")
            .password(passwordEncoder.encode("p1"))
            .roles("user")
            .and()
            .withUser("u2")
            .password(passwordEncoder.encode("p2"))
            .roles("user", "customer")
        // real system
//        auth.jdbcAuthentication() //-> need valid dataSource (?)
//        auth.userDetailsService()
//        auth.parentAuthenticationManager()
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
            .formLogin()
            .loginProcessingUrl("/auth/login")
        .and()
            .logout()
            .logoutUrl("/auth/logout")


        http.csrf().disable() //value not sent in logout form
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
//        http.cors().disable()
        //http.cors() //only javascript can make request because coming from us
    }
}
package it.polito.ecommerce.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class SecurityConfiguration(val passwordEncoder: PasswordEncoder): WebSecurityConfigurerAdapter() {

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
        //super.configure(http)
        //tutti quelli che iniziano con secure devono essere autenticati
        //.antMatchers("/secure/**")
        http
            .authorizeRequests()
            .antMatchers("/")
            .permitAll()

        http.csrf().disable() //value not sent in logout form
        //http.cors() //only javascript can make request because caming from us
    }
}
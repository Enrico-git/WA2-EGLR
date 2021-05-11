package it.polito.ecommerce

import it.polito.ecommerce.security.JwtUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@SpringBootApplication
@EnableScheduling
class EcommerceApplication {

    @Value("\${spring.mail.host}")
    val host: String = ""

    @Value("\${spring.mail.port}")
    val port: Int = 0

    @Value("\${spring.mail.username}")
    val username: String = ""

    @Value("\${spring.mail.password}")
    val password: String = ""

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    val auth: String = ""

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    val starttlsEnable: String = ""

    @Value("\${spring.mail.properties.mail.debug}")
    val debug: String = ""

    @Bean
    fun getMailSender(): JavaMailSender {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.host = host
        javaMailSender.port = port
        javaMailSender.username = username
        javaMailSender.password = password
        val props: Properties = javaMailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = auth
        props["mail.smtp.starttls.enable"] = starttlsEnable
        props["mail.debug"] = debug

        return javaMailSender
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}

fun main(args: Array<String>) {
    runApplication<EcommerceApplication>(*args)
}

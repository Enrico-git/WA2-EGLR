package it.polito.ecommerce

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@SpringBootApplication
@EnableScheduling
class EcommerceApplication{

    @Value("\${spring.mail.host}")
    private val host: String = ""

    @Value("\${spring.mail.port}")
    private val port: Int = -1

    @Value("\${spring.mail.username}")
    private val username: String = ""

    @Value("\${spring.mail.password}")
    private val password: String = ""

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    private val auth: String = ""

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    private val starttlsEnable: String = ""

    @Value("\${spring.mail.properties.mail.debug}")
    private val debug: String = ""

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
        props["mail.transport.protocol"] = starttlsEnable
        props["mail.transport.protocol"] = debug

        return javaMailSender
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        //tool for support hashing password
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}

fun main(args: Array<String>) {
    runApplication<EcommerceApplication>(*args)
}

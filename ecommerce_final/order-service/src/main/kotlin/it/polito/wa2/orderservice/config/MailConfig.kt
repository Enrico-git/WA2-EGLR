package it.polito.wa2.orderservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class MailConfig {
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
}
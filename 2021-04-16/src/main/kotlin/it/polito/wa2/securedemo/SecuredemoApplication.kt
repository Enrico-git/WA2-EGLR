package it.polito.wa2.securedemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication
class SecuredemoApplication{

	@Bean
	fun passwordEncoder(): PasswordEncoder{
		//tool for support hashing password
		return PasswordEncoderFactories.createDelegatingPasswordEncoder()
	}
}

fun main(args: Array<String>) {
	runApplication<SecuredemoApplication>(*args)
}

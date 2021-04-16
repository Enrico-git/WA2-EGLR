package it.polito.wa2.securedemo.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class MainController {

    @GetMapping("/")
    fun root(principal: Principal?) = "Hello, ${principal?.name?:"Pino"}"

    @GetMapping("/secure")
    fun secure(principal: Principal) = "Secured content for ${principal.name}"

}
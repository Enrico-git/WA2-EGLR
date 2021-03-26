package it.polito.wa2.demo

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
class MainController {
//    @GetMapping("/")
//    fun homePage():String {
//        return "index"
//    }
    @GetMapping("/")
    fun homePage(model: Model):String {
        val d = Date()
        model.addAttribute("date", d.toString())
        model.addAttribute("name", "pollo")
        return "index"
    }

    @GetMapping("/search")
    fun search(@RequestParam("search") text: String,
    model: Model): String{
        model.addAttribute("text", text)
        model.addAttribute("results", listOf(Math.random(), Math.random()))
        return "results"
    }

    @GetMapping("/list")
    fun list(model: Model): String{
        model.addAttribute("l", listOf("Alpha", "Beta", "Gamma", "Delta"))
        return "list"
    }

    @GetMapping("/reserve/{month}/{day}")
    fun reservations(@PathVariable month: Int,
        @PathVariable day: Int,
        model: Model): String{
        model.addAttribute("date", "$month/$day")
        model.addAttribute("slots", listOf("8:30", "9:45", "11:45"))
        return "reservation"
    }

    @GetMapping("/register")
    fun getRegisterForm(registration: Registration) = "register"

    @PostMapping("/register")
    fun processRegisterForm(model: Model, registration: Registration): String{
        model.addAttribute("name", registration.name)
        model.addAttribute("age", registration.age)
        return "result"
    }

}

data class Registration(val name: String ="", val age: Int=18)
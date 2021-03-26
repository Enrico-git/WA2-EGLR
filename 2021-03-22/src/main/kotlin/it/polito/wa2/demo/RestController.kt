package it.polito.wa2.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {

    @GetMapping("/api/list")
    fun listData() : List<String> {
        return listOf("Alpha", "Beta", "Gamma", "Delta")
    }
    @GetMapping("/api/random")
    fun random() : C{
        return C("xyz", Math.random())
    }
}

data class C(val a:String, val b: Double)
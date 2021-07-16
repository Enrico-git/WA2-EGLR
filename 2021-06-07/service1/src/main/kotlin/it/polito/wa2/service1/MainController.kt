package it.polito.wa2.service1

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class MainController {

    @GetMapping("")
    fun root(): List<String>{
        return listOf("a", "b", "c", "d")
    }

    @GetMapping("/date")
    fun date(): String {
        return Date().toString()
    }

    // http://localhost:8100/date
}

package it.polito.wa2.service2

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class Service2Controller {

    @GetMapping("")
    fun main() : MyInfo{
        return MyInfo("Service2", Date())
    }

}

data class MyInfo(val name: String, val date: Date)

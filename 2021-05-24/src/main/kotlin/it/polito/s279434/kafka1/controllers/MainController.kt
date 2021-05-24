package it.polito.s279434.kafka1.controllers

import it.polito.s279434.kafka1.dto.RequestDTO
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController(val kafkaTemplate: KafkaTemplate<String, RequestDTO>) {
    @PostMapping
    fun process(@RequestBody requestDTO: RequestDTO) {
        kafkaTemplate.send("Requests", requestDTO).get()
    }

    //TODO from terminal
    // http -j :8080 id=3 msg="This is a message fro kafka topic named request"
}
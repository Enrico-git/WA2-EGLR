package it.polito.s279434.kafka1.listeners

import it.polito.s279434.kafka1.dto.RequestDTO
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class RequestListener {
    @KafkaListener(id="myGroup", topics=["Requests"], containerFactory = "requestDTOContainerFactory")
    fun listen(requestDTO: RequestDTO){
        println("Received --> $requestDTO")
        Thread.sleep(5000)
    }
}
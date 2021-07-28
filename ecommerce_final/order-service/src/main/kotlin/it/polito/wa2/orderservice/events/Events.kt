package it.polito.wa2.orderservice.events

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.statemachine.StateMachineImpl
import org.springframework.context.ApplicationEvent

class StateMachineEvent(source: StateMachineImpl, val event: String) : ApplicationEvent(source)
class SagaFinishedEvent(source: StateMachineImpl) : ApplicationEvent(source)
class SagaFailureEvent(source: StateMachineImpl) : ApplicationEvent(source)
class KafkaResponseReceivedEventInResponseTo(source: StateMachineImpl, val event: StateMachineEvents) : ApplicationEvent(source)
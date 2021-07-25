package it.polito.wa2.orderservice.events

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.statemachine.StateMachine
import org.springframework.context.ApplicationEvent

class StateMachineEvent(source: StateMachine, val event: String) : ApplicationEvent(source)
class SagaFinishedEvent(source: StateMachine) : ApplicationEvent(source)
class SagaFailureEvent(source: StateMachine) : ApplicationEvent(source)
class KafkaResponseReceivedEventInResponseTo(source: StateMachine, val event: StateMachineEvents) : ApplicationEvent(source)
package it.polito.wa2.orderservice.domain

import it.polito.wa2.orderservice.common.StateMachineEvents
import it.polito.wa2.orderservice.common.StateMachineStates

data class Transition(
    var source: StateMachineStates?,
    var target: StateMachineStates?,
    var event: StateMachineEvents?,
    var isRollingBack: Boolean = false,
    var isPassive: Boolean = false,
    var action: (() -> Any?)?
)

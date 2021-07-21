package it.polito.wa2.orderservice.domain

import kotlinx.coroutines.Job

data class OrderJob(
    val first: String,
    val second: Job
)
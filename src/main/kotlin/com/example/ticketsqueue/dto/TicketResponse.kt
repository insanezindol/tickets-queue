package com.example.ticketsqueue.dto

data class TicketResponse(
    val uuid: String,
    val ticketNumber: Long,
    val timestamp: Long = System.currentTimeMillis()
)

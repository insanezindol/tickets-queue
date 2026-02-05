package com.example.ticketsqueue.controller

import com.example.ticketsqueue.dto.TicketResponse
import com.example.ticketsqueue.service.TicketService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TicketController(
    private val ticketService: TicketService
) {

    private val log = LoggerFactory.getLogger(this.javaClass)!!

    @GetMapping("/ticket")
    suspend fun getTicket(@RequestParam userId: String): TicketResponse {
        log.info("[BEG] userId: $userId")
        // val ticketResponse = TicketResponse(uuid = "test", ticketNumber = 1);
        val ticketResponse = ticketService.allocateTicket(userId)
        log.info("[END] userId: $userId, ticketResponse: $ticketResponse")
        return ticketResponse
    }

}
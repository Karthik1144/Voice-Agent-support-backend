package com.support.backend.controller;

import com.support.backend.dto.CreateTicketRequest;
import com.support.backend.dto.TicketResponse;
import com.support.backend.dto.UpdateTicketStatusRequest;
import com.support.backend.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // GET /api/tickets/TK1024
    @GetMapping("/{ticketCode}")
    public TicketResponse getTicket(@PathVariable String ticketCode) {
        return TicketResponse.from(ticketService.getByTicketCode(ticketCode));
    }

    // POST /api/tickets
    @PostMapping
    public TicketResponse createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return TicketResponse.from(ticketService.createTicket(request));
    }

    // PUT /api/tickets/TK1024/status
    @PutMapping("/{ticketCode}/status")
    public TicketResponse updateStatus(@PathVariable String ticketCode,
                                        @Valid @RequestBody UpdateTicketStatusRequest request) {
        return TicketResponse.from(
                ticketService.updateStatus(ticketCode, request.status, request.note));
    }

    // POST /api/tickets/TK1024/close
    @PostMapping("/{ticketCode}/close")
    public TicketResponse closeTicket(@PathVariable String ticketCode) {
        return TicketResponse.from(ticketService.closeTicket(ticketCode));
    }

    // POST /api/tickets/TK1024/escalate
    @PostMapping("/{ticketCode}/escalate")
    public TicketResponse escalateTicket(@PathVariable String ticketCode,
                                          @RequestBody(required = false) UpdateTicketStatusRequest request) {
        String note = request != null ? request.note : null;
        return TicketResponse.from(ticketService.escalateTicket(ticketCode, note));
    }
}

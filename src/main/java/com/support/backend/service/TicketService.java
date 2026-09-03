package com.support.backend.service;

import com.support.backend.dto.CreateTicketRequest;
import com.support.backend.entity.*;
import com.support.backend.repository.OrderRepository;
import com.support.backend.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Ticket business logic lives here, not in the controller and never
 * in the AI. The agent can only request actions through the API -
 * this class decides whether the action is actually allowed.
 */
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final CustomerService customerService;

    public TicketService(TicketRepository ticketRepository,
                          OrderRepository orderRepository,
                          CustomerService customerService) {
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.customerService = customerService;
    }

    @Transactional
    public Ticket createTicket(CreateTicketRequest request) {
        Customer customer = customerService.findOrCreateByPhone(
        request.customerPhone, request.customerName);

        // Order linkage is best-effort and must never block ticket creation.
        // orderCode comes from a voice pipeline (STT -> LLM tool call), so
        // stray whitespace or casing ("ORD 123" vs "ORD123") is expected and
        // normalized here. If the code still can't be resolved to a real
        // order, we don't fail the whole request - we create the ticket
        // without the order link and record what was said, so a human can
        // follow up instead of the caller's ticket silently never existing.
        Order order = null;
        String unresolvedOrderNote = null;
        if (request.orderCode != null && !request.orderCode.isBlank()) {
            String normalizedCode = request.orderCode.trim().replaceAll("\\s+", "");
            order = orderRepository.findByOrderCodeIgnoreCase(normalizedCode).orElse(null);
            if (order == null) {
                unresolvedOrderNote = "Caller mentioned order code \"" + request.orderCode
                        + "\" but it could not be matched to an order on file.";
            }
        }

        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setOrder(order);
        ticket.setSubject(request.subject);
        ticket.setDescription(appendNote(request.description, unresolvedOrderNote));
        ticket.setPriority(parsePriority(request.priority));
        ticket.setStatus(TicketStatus.NEW);

        // Save once to get an auto-generated id, then assign the
        // human-readable ticket code and save again.
        ticket = ticketRepository.save(ticket);
        ticket.setTicketCode("TK" + (1000 + ticket.getId()));
        return ticketRepository.save(ticket);
    }

    public Ticket getByTicketCode(String ticketCode) {
        return ticketRepository.findByTicketCodeIgnoreCase(ticketCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No ticket found with code " + ticketCode));
    }

    @Transactional
    public Ticket updateStatus(String ticketCode, String statusRaw, String note) {
        Ticket ticket = getByTicketCode(ticketCode);
        TicketStatus newStatus = parseStatus(statusRaw);

        // Simple business rule: a ticket that's already CLOSED can't be
        // silently reopened by a status update - that should go through
        // a human, not the AI. Everything else is allowed for the MVP.
        if (ticket.getStatus() == TicketStatus.CLOSED && newStatus != TicketStatus.CLOSED) {
            throw new IllegalStateException(
                    "Ticket " + ticketCode + " is already closed and cannot be reopened automatically.");
        }

        ticket.setStatus(newStatus);
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(Instant.now());
        }
        if (newStatus == TicketStatus.CLOSED) {
            ticket.setClosedAt(Instant.now());
        }
        // 'note' is accepted for future use (ticket_events audit log) -
        // not persisted yet in this MVP schema.

        return ticketRepository.save(ticket);
    }

    public Ticket closeTicket(String ticketCode) {
        return updateStatus(ticketCode, TicketStatus.CLOSED.name(), "Closed");
    }

    public Ticket escalateTicket(String ticketCode, String note) {
        return updateStatus(ticketCode, TicketStatus.ESCALATED.name(), note);
    }

    private String appendNote(String description, String note) {
        if (note == null) return description;
        if (description == null || description.isBlank()) return note;
        return description + "\n\n" + note;
    }

    private TicketPriority parsePriority(String raw) {
        if (raw == null || raw.isBlank()) return TicketPriority.MEDIUM;
        try {
            return TicketPriority.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid priority: " + raw +
                    ". Must be one of LOW, MEDIUM, HIGH, URGENT.");
        }
    }

    private TicketStatus parseStatus(String raw) {
        try {
            return TicketStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + raw +
                    ". Must be one of NEW, OPEN, IN_PROGRESS, WAITING_FOR_CUSTOMER, RESOLVED, CLOSED, ESCALATED.");
        }
    }
}

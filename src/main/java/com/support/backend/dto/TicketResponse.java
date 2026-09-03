package com.support.backend.dto;

import com.support.backend.entity.Ticket;

public class TicketResponse {
    public String ticketCode;
    public String subject;
    public String description;
    public String priority;
    public String status;
    public String orderCode;
    public String customerName;

    public static TicketResponse from(Ticket ticket) {
        TicketResponse r = new TicketResponse();
        r.ticketCode = ticket.getTicketCode();
        r.subject = ticket.getSubject();
        r.description = ticket.getDescription();
        r.priority = ticket.getPriority().name();
        r.status = ticket.getStatus().name();
        r.orderCode = ticket.getOrder() != null ? ticket.getOrder().getOrderCode() : null;
        r.customerName = ticket.getCustomer().getName();
        return r;
    }
}

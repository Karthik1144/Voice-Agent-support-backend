package com.support.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateTicketStatusRequest {
    // "NEW" | "OPEN" | "IN_PROGRESS" | "WAITING_FOR_CUSTOMER" | "RESOLVED" | "CLOSED" | "ESCALATED"
    @NotBlank
    public String status;

    // Optional free-text note, e.g. why it's being escalated
    public String note;
}

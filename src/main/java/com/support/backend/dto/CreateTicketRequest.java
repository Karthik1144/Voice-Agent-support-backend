package com.support.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTicketRequest {

    // Customer is identified by phone number, since that's what the voice
    // agent naturally has from the call - avoids needing internal customer IDs
    @NotBlank
    public String customerPhone;

    // Used only if no customer exists yet for this phone number - a new
    // customer record is created automatically using this name. Optional
    // if the customer already exists.
    public String customerName;

    // Optional - not every ticket is tied to an order
    public String orderCode;

    @NotBlank
    public String subject;

    public String description;

    // "LOW" | "MEDIUM" | "HIGH" | "URGENT" - defaults to MEDIUM if omitted
    public String priority;
}

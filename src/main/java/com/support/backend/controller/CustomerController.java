package com.support.backend.controller;

import com.support.backend.entity.Customer;
import com.support.backend.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // GET /api/customers/by-phone/+919876543210
    // Voice agents naturally have the caller's phone number, so lookup
    // is by phone rather than internal numeric id.
    @GetMapping("/by-phone/{phone}")
    public Customer getByPhone(@PathVariable String phone) {
        return customerService.getByPhone(phone);
    }
}

package com.support.backend.service;

import com.support.backend.entity.Customer;
import com.support.backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getByPhone(String phone) {
        return customerRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with phone " + phone));
    }

    public Customer findOrCreateByPhone(String phone, String name) {
        return customerRepository.findByPhone(phone)
                .orElseGet(() -> {
                    Customer customer = new Customer();
                    customer.setPhone(phone);
                    customer.setName(name != null && !name.isBlank() ? name : "Unknown Caller");
                    return customerRepository.save(customer);
                });
    }
}

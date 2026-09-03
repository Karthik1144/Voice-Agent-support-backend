package com.support.backend.config;

import com.support.backend.entity.Customer;
import com.support.backend.entity.Order;
import com.support.backend.entity.OrderStatus;
import com.support.backend.repository.CustomerRepository;
import com.support.backend.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds a couple of sample customers/orders on every startup, but only
 * if the database is empty - so it's safe to restart the app repeatedly
 * during development without creating duplicates.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public DataSeeder(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return;
        }

        Customer rahul = new Customer();
        rahul.setName("Rahul Sharma");
        rahul.setPhone("+919876543210");
        rahul.setEmail("rahul@example.com");
        rahul = customerRepository.save(rahul);

        Customer priya = new Customer();
        priya.setName("Priya Verma");
        priya.setPhone("+919812345678");
        priya.setEmail("priya@example.com");
        priya = customerRepository.save(priya);

        Order ord123 = new Order();
        ord123.setOrderCode("ORD123");
        ord123.setCustomer(rahul);
        ord123.setStatus(OrderStatus.DELAYED);
        ord123.setAmount(new BigDecimal("1499.00"));
        ord123.setExpectedDelivery(LocalDate.now().plusDays(2));
        orderRepository.save(ord123);

        Order ord124 = new Order();
        ord124.setOrderCode("ORD124");
        ord124.setCustomer(priya);
        ord124.setStatus(OrderStatus.SHIPPED);
        ord124.setAmount(new BigDecimal("899.00"));
        ord124.setExpectedDelivery(LocalDate.now().plusDays(1));
        orderRepository.save(ord124);

        System.out.println("Seed data created: customers ORD123 (Rahul, DELAYED), ORD124 (Priya, SHIPPED)");
    }
}
